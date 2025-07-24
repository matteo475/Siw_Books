package esame.service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import esame.DTOmodel.RecensioneDTO;
import esame.model.Libro;
import esame.model.Recensione;
import esame.model.User;
import esame.repository.LibroRepository;
import esame.repository.RecensioneRepository;
import esame.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class RecensioneService {

	@Autowired
	private LibroRepository libroRepo;

	@Autowired
	private RecensioneRepository recRepo;

	// Se usi login:
	@Autowired
	private UserRepository utenteRepo;

	@Transactional
	public void aggiungiRecensione(Long libroId, RecensioneDTO dto, String surname) {
		// 1) carica il libro o fallisci
		Libro libro = libroRepo.findById(libroId)
				.orElseThrow(() -> new EntityNotFoundException("Libro non trovato: " + libroId));

		// 2) carica l'utente (usa findBySurname)
		User utente = utenteRepo.findBySurname(surname)
				.orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + surname));

		// Costruisci la recensione
		Recensione rec = new Recensione();
		rec.setTesto(dto.getTesto());
		rec.setValutazione(dto.getValutazione());
		rec.setDataCreazione(dto.getDataCreazione());
		rec.setLibro(libro);
		rec.setUser(utente);

		// Salva
		recRepo.save(rec);
	}

	@Transactional
	public void deleteRecensione(Long libroId, Long utenteId) {
		recRepo.deleteByLibroAndUtente(libroId, utenteId);
	}
	
	public void deleteRecensione(Long recensioneId) {
        Recensione r = recRepo.findById(recensioneId)
            .orElseThrow(() -> new EntityNotFoundException("Recensione non trovata"));
        recRepo.delete(r);
    }

	/*
	 * posso fare anche tramite il service senza utilizzare la query nella
	 * repository
	 */
	@Transactional
	public void removeRecensioniByUtente(Long libroId, Long utenteId) {
		Libro libro = libroRepo.findById(libroId)
				.orElseThrow(() -> new EntityNotFoundException("Libro non trovato: " + libroId));

		libro.getRecensioni().removeIf(r -> r.getUser().getId().equals(utenteId));

		// il save non è strettamente necessario se sei in Persistence Context,
		// ma lo mettiamo per chiarezza
		// libroRepo.save(libro);
	}
	/*
	 * Con orphanRemoval, basta rimuovere dall’ArrayList; JPA si occupa del DELETE.
	 */

	@Transactional
	public void deleteById(Long recensioneId) {
		recRepo.deleteById(recensioneId);
	}

	/**
	 * Elimina una recensione solo se l'username corrisponde all'autore.
	 *
	 * @param recensioneId id della recensione da cancellare
	 * @param userSurname  cognome/username del chiamante
	 * @throws EntityNotFoundException se la recensione non esiste
	 * @throws AccessDeniedException   se l'utente non è l'autore
	 */
	@Transactional
	public void deleteRecensioneIfOwner(Long recensioneId, String userSurname) throws AccessDeniedException {
		Recensione rec = recRepo.findById(recensioneId)
				.orElseThrow(() -> new EntityNotFoundException("Recensione non trovata: " + recensioneId));

		// controllo che il cognome dell'utente della recensione corrisponda
		if (!rec.getUser().getSurname().equals(userSurname)) {
			throw new AccessDeniedException("Non sei autorizzato a cancellare questa recensione");
		}

		recRepo.delete(rec);
	}

}
