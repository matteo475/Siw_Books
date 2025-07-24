package esame.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import esame.repository.AutoreRepository;
import esame.repository.ImmagineRepository;
import esame.repository.LibroRepository;
import esame.service.ImmagineService;
import esame.service.LibroService;
import it.uniroma3.Ecommerce.DTOmodel.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import esame.DTOmodel.LibroDTO;
import esame.model.*;

/* classe controller che gestisce le interazioni 
 * 
 * 	ADMIN ----------> LIBRO
 * 
 * */

@Controller
public class AdminToLibroController {

	@Autowired
	LibroRepository libroRepository;

	@Autowired
	LibroService libroService;

	@Autowired
	AutoreRepository autoreRepository;

	@Autowired
	ImmagineRepository immagineRepository;

	@Autowired
	ImmagineService immagineService;

	/**
	 * metodo per visualizzare la lista dei libri inseriti
	 * 
	 * @param modello come dati da passare alla pagina per visualizzare i libri
	 **/
	@GetMapping("/admin/libri")
	public String showListaLibri(Model model) {
		List<Libro> libri = libroRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("libro", libri);
		return "admin/showLibro";
	}

	/**
	 * metodo per entrare nella pagina per aggiungere un libro
	 * 
	 * @param modello come dati da passare alla pagina di aggiunta del libro
	 **/
	@GetMapping("/admin/creaLibro")
	public String showCreatePageLibro(Model model) {
		LibroDTO dto = new LibroDTO();
		model.addAttribute("libroDTO", dto);

		// questa riga è per inserire gli autori da mettere
		model.addAttribute("allAutori", autoreRepository.findAll(Sort.by("cognome")));

		return "admin/newLibro";
	}

	/**
	 * meotodo per gestire l'inizializzazione del libro da parte dell'admin
	 * 
	 * @param LibroDTO
	 * @return entità Libro salvata e ritorno alla visualizzazione di tutti i libri
	 * @throws IOException
	 **/
	// 3) Salva Libro + immagini dal DTO
	@PostMapping(path = "/admin/creaLibro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String creaLibro(@Valid @ModelAttribute("libroDTO") LibroDTO dto, BindingResult result,
			RedirectAttributes redirectAttrs) throws IOException {

		// controllo se ci sono errori
		if (result.hasErrors()) {
			return "admin/indexAdmin.html";
		}

		// passo i valori da libroDTO a libro
		Libro libro = new Libro();
		libro.setTitolo(dto.getTitolo());
		libro.setAnnoPubblicazione(dto.getAnnoPubblicazione());
		libro.setGenere(dto.getGenere());
		libro.setTrama(dto.getTrama());

		// devo associare gli autori selezionati al libro
		for (Long autoreId : dto.getAutoreId()) {
			Autore autore = autoreRepository.findById(autoreId)
					.orElseThrow(() -> new EntityNotFoundException("Autore non trovato: " + autoreId));
			libro.addAutore(autore);
		}

		// creo l'immagine
		// 2) per ogni file non vuoto, crea e associa un'immagine
		MultipartFile[] files = dto.getFiles();
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					Immagine img = new Immagine();
					img.setNomeFile(file.getOriginalFilename());
					img.setTipoContenuto(file.getContentType());
					img.setDati(file.getBytes()); // <-- byte[] corretto
					// immagineService.salva(img);
					libro.addImmagine(img);
				}
			}
		}

		// salvo l'entita
		libroService.save(libro);
		redirectAttrs.addFlashAttribute("success", "Libro creato con successo");

		return "redirect:/admin/libri";
	}

	/**
	 * metodo per gestire l'inizializzazione dell'immagine del libro
	 * 
	 * @param id       dell'immagine
	 * @param immagine da inserire
	 **/
	@PostMapping("/admin/libro/{id}/immagineLibro")
	public String uploadImmagineLibro(@PathVariable Long id, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttribute) {

		try {
			libroService.aggiungiImmagine(id, file);
			redirectAttribute.addFlashAttribute("message", "Immagine caricata con successo");
		} catch (Exception e) {
			redirectAttribute.addFlashAttribute("error", "Upload fallito: " + e.getMessage());
		}
		return "redirect:/admin/libri";
	}

	@GetMapping("/admin/libro/{lid}/immagine/{iid}")
	public ResponseEntity<byte[]> serveImage(@PathVariable("iid") Long imageId) {

		Immagine img = immagineRepository.findById(imageId)
				.orElseThrow(() -> new EntityNotFoundException("Immagine non trovata"));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(img.getTipoContenuto())).body(img.getDati());
	}

	@GetMapping("/admin/libro/{lid}")
	public String showDettaglio(@PathVariable Long lid, Model model) {
		Libro libro = libroService.caricaConRecensioni(lid);
		model.addAttribute("libro", libro);
		return "admin/editLibro";
	}

	/**
	 * metodo che permette di modificare le informazioni sui libri dato l'id
	 * 
	 * @param modello come dati da passare alla pagina di modifica del libro
	 * @param id      del libro da modificare
	 * @return la pagina dove posso modificare il libro
	 **/
	@GetMapping("/admin/editLibro")
	public String showLibroEditPage(Model model, @RequestParam Long id) {

		try {

			// mi prendo il libro corrispondente all'id
			Libro libro = libroRepository.findById(id).get();
			model.addAttribute("libro", libro);

			// creo il nuovo libro transiente
			LibroDTO dto = new LibroDTO();
			dto.setTitolo(libro.getTitolo());
			dto.setAnnoPubblicazione(libro.getAnnoPubblicazione());
			dto.setId(libro.getId());
			dto.setTitolo(libro.getTitolo());
			dto.setAnnoPubblicazione(libro.getAnnoPubblicazione());
			dto.setTrama(libro.getTrama());
			dto.setGenere(libro.getGenere());
			model.addAttribute("libroDTO", dto);
			model.addAttribute("libro", libro);
			model.addAttribute("allAutori", autoreRepository.findAll());

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "redirect:/admin/libri";
		}

		return "admin/editLibro";
	}

	/**
	 * metodo che gestisce cosa fare una volta modificato il libro dato il suo id
	 * 
	 * @param modello coem dati da passare per modificare i dati del libro
	 * @param id      del libro da modificare
	 * @param oggetto transiente da modificare
	 **/

	@PostMapping("/admin/editLibro")
	public String updateLibro (Model model, @RequestParam Long id, @Valid @ModelAttribute LibroDTO dto,
	BindingResult result) {

		if (result.hasErrors()) {
			return "admin/editLibro"; // torna alla stessa form
		}

		// recupera l'entità, aggiorna ed esegui save()
		Libro libro = libroRepository.findById(id).get();
		model.addAttribute("libro", libro);

		libro.setTitolo(dto.getTitolo());
		libro.setAnnoPubblicazione(dto.getAnnoPubblicazione());
		libro.setTrama(dto.getTrama());
		libro.setGenere(dto.getGenere());
		// (gestisci anche autori o immagini, se necessario)
		libroService.save(libro);

		//redirect.addFlashAttribute("success", "Modifiche salvate!");
		return "redirect:/admin/libri";
	}

	/*
	 * @PostMapping("/admin/editLibro") public String updateLibro(Model
	 * model, @RequestParam Long id, @Valid @ModelAttribute LibroDTO dto,
	 * BindingResult result) {
	 * 
	 * // mi accerto di essere connesso al database try {
	 * 
	 * // prendo il libro a partire dal suo id Libro libro =
	 * libroRepository.findById(id).get(); model.addAttribute("libro", libro);
	 * 
	 * // verifico se i parametri del form sono validi if (result.hasErrors()) {
	 * return "admin/indexAdmin.html"; }
	 * 
	 * // se non ci sono errori aggiorno i valori del libro
	 * libro.setTitolo(dto.getTitolo());
	 * libro.setAnnoPubblicazione(dto.getAnnoPubblicazione());
	 * 
	 * // salvo le modifiche libroRepository.save(libro); } catch (Exception ex) {
	 * System.out.println("Exception: " + ex.getMessage()); } return
	 * "redirect:/admin/libri"; }
	 */

	/**
	 * metodo per cancellare un libro dato il suo id
	 * 
	 * @param id del libro da cancellare
	 * @return libro cancellato e mostra la pagina con la tabella dei libri
	 **/
	@GetMapping("/admin/deleteLibro")
	public String deleteLibro(@RequestParam Long id) {

		try {

			// prendo il libro corrente (quello corrispondente all'id passato)
			Libro libro = libroRepository.findById(id).get();

			// elimino l'oggetto libro vero e proprio
			libroRepository.delete(libro);

		} catch (Exception ex) {
			System.out.println("Exception : " + ex.getMessage());
		}
		return "redirect:/admin/libri";
	}

}
