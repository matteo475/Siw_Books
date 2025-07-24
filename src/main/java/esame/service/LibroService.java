package esame.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import esame.DTOmodel.LibroDTO;
import esame.model.Immagine;
import esame.model.Libro;
import esame.model.Recensione;
import esame.model.User;
import esame.repository.ImmagineRepository;
import esame.repository.LibroRepository;
import esame.repository.RecensioneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class LibroService {

	@Autowired
	LibroRepository libroRepository;

	@Autowired
	ImmagineRepository immagineRepository;

	@Autowired
	private RecensioneRepository recensioneRepository;

	/**
	 * metodo per salvare l'entità libro
	 * 
	 * @param l'oggetto Libro da salvare
	 **/
	public void save(Libro libro) {
		libroRepository.save(libro);
	}

	/**
	 * metodo per vedere tutti gli autori
	 * 
	 * @param id degli autori
	 * @throws Exception
	 **/
	public Libro get(Long id) throws Exception {
		Optional<Libro> result = libroRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		throw new Exception("Non troviamo nessun libro con ID : " + id);
	}

	public Libro caricaConRecensioni(Long libroId) {
		// Recupera l'Optional dal repository
		Optional<Libro> optLibro = libroRepository.findById(libroId);

		// Se non presente, solleva l'eccezione
		if (!optLibro.isPresent()) {
			throw new EntityNotFoundException("Libro non trovato");
		}

		// Estrae il libro, forza il caricamento delle recensioni e lo restituisce
		Libro libro = optLibro.get();
		libro.getRecensioni().size(); // forza fetch delle recensioni
		return libro;
	}

	public Recensione aggiungiRecensione(Long libroId, User utente, String testo, int voto) {
		Libro libro = caricaConRecensioni(libroId);
		Recensione r = new Recensione();
		r.setLibro(libro);
		r.setUser(utente);
		r.setTesto(testo);
		r.setValutazione(voto);
		return recensioneRepository.save(r);
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
		Libro libro = libroRepository.findById(libroId)
				.orElseThrow(() -> new EntityNotFoundException("Libro non trovato"));

		Immagine img = new Immagine();
		img.setNomeFile(file.getOriginalFilename());
		img.setTipoContenuto(file.getContentType());
		img.setDati(file.getBytes());

		// associa e salva
		libro.addImmagine(img);
		// grazie a CascadeType.ALL, salva anche l’immagine
		libroRepository.save(libro);
	}

	@Transactional
	public Immagine addImage(Long bookId, MultipartFile file) throws IOException {
		Libro book = libroRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
		Immagine img = new Immagine();
		img.setNomeFile(file.getOriginalFilename());
		img.setDati(file.getBytes());
		img.setLibro(book);
		return immagineRepository.save(img);
	}

	@Transactional
	public void creaLibroConImmagini(LibroDTO dto) throws IOException {
		Libro libro = new Libro();
		libro.setTitolo(dto.getTitolo());
		libro.setAnnoPubblicazione(dto.getAnnoPubblicazione());

		if (dto.getFiles() != null) {
			for (MultipartFile file : dto.getFiles()) {
				if (!file.isEmpty()) {
					Immagine img = new Immagine();
					img.setNomeFile(file.getOriginalFilename());
					img.setTipoContenuto(file.getContentType());
					img.setDati(file.getBytes()); // ← byte[]
					img.setLibro(libro);
					libro.getImmagini().add(img);
				}
			}
		}

		libroRepository.save(libro);
	}

	/**
	 * Recupera un Libro con tutte le recensioni e gli autori già fetch-ati, per
	 * evitare problemi di lazy-loading in Thymeleaf.
	 *
	 * @param id l'id del libro
	 * @return l'entità Libro popolata di recensioni e autori
	 * @throws EntityNotFoundException se non esiste un libro con quell'id
	 */
	@Transactional
	public Libro findByIdWithRecensioniAndAutori(Long id) {
		return libroRepository.findByIdWithRecensioniAndAutori(id)
				.orElseThrow(() -> new EntityNotFoundException("Libro non trovato con id " + id));
	}
	
	
	/**
	 * metodo per creare la barra di ricerca e fare una ricerca in base a una keyword
	 * @param keyword sulla quale fare la ricerca
	 * */
	public List<Libro> findByTitolo(String keyword){
		return libroRepository.findByTitoloContainingIgnoreCase(keyword);
	}

	public List<Libro> findAll(){
		return libroRepository.findAll();
	}
}
