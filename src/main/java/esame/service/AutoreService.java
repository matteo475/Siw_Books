package esame.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import esame.model.Autore;
import esame.model.Immagine;
import esame.model.Libro;
import esame.repository.AutoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AutoreService {

	@Autowired
	private AutoreRepository autoreRepository;

	/**
	 * metodo per salvare il autore
	 **/
	public void save(Autore autore) {
		autoreRepository.save(autore);
	}

	/**
	 * metodo per vedere tutti gli autori per id
	 * 
	 * @throws Exception
	 **/
	public Autore get(Long id) throws Exception {
		Optional<Autore> result = autoreRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		throw new Exception("Non troviamo nessun autore con ID : " + id);
	}

	/**
	 * metodo per aggiungere l'immagine al libro
	 * 
	 * @param id         del libro al quale aggiungere l'immagine
	 * @param l'immagine da aggiungere
	 **/
	@Transactional
	public void aggiungiImmagine(Long libroId, MultipartFile file) throws IOException {

		// prendo l'oggetto libro
		Autore autore = autoreRepository.findById(libroId)
				.orElseThrow(() -> new EntityNotFoundException("Libro non trovato"));

		Immagine img = new Immagine();
		img.setNomeFile(file.getOriginalFilename());
		img.setTipoContenuto(file.getContentType());
		img.setDati(file.getBytes());

		// associa e salva
		autore.addImmagineAutore(img);
		// grazie a CascadeType.ALL, salva anche l’immagine
		autoreRepository.save(autore);
	}

	public Autore findById(Long id) {
		return autoreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Autore non trovato: " + id));
	}

}
