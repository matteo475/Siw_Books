package esame.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import esame.DTOmodel.AutoreDTO;
import esame.model.Autore;
import esame.model.Immagine;
import esame.repository.*;
import esame.service.AutoreService;
import jakarta.validation.Valid;

/*classe controller che gestisce le interazioni 
 * 
 * 	ADMIN ----> AUTORE
 *
 **/

@Controller
public class AdminToAutoreController {

	@Autowired
	AutoreRepository autoreRepository;

	@Autowired
	AutoreService autoreService;

	@GetMapping("/admin/indexAdmin")
	public String showHomePageAdmin() {
		return "admin/indexAdmin.html";
	}

	/**
	 * metodo per visualizzare la lista degli autori inseriti
	 **/
	@GetMapping("/admin/autori")
	public String showListAutori(Model model) {
		List<Autore> autori = autoreRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		model.addAttribute("autore", autori);
		return "admin/showAutore.html";
	}

	/**
	 * metodo per entrare nella pagina per aggiungere un autore
	 **/
	@GetMapping("/admin/creaAutore")
	public String showCreatePageAutore(Model model) {
		AutoreDTO dto = new AutoreDTO();
		model.addAttribute("autoreDTO", dto);
		return "admin/newAutore.html";
	}

	/**
	 * metodo per gestire l'inizializzazione dell'autore da parte dell'admin
	 * 
	 * @param AutoreDTO
	 * @return entità autore salvata e ritorno alla visualizzazione di tutti gli
	 *         autoris
	 **/
	@PostMapping(path = "/admin/creaAutore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String creaAutore(@Valid @ModelAttribute("autoreDTO") AutoreDTO dto, BindingResult result,
			RedirectAttributes redirectAttrs) throws IOException {

		// controllo se ci sono errori
		if (result.hasErrors()) {
			return "admin/indexAdmin.html";
		}
		
		/* passo i valori da autoreDTO a autore */
		Autore autore = new Autore();
		autore.setNome(dto.getNome());
		autore.setCognome(dto.getCognome());
		autore.setAnnoMorte(dto.getAnnoMorte());
		autore.setAnnoNascita(dto.getAnnoNascita());
		autore.setNazionalita(dto.getNazionalita());
		autore.setBiografia(dto.getBiografia());

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
					autore.addImmagineAutore(img);
				}
			}
		}

		// salvo l'entita
		autoreService.save(autore);
		redirectAttrs.addFlashAttribute("success", "Libro creato con successo");
		
		return "redirect:/admin/autori";
	}

	/**
	 * metodo che permette di modificare le informazioni sugli autori dato l'id
	 * 
	 * @param modello come dati da passare alla pagina di modifica dell'autore
	 * @param id dell'autore da modificare
	 * @return la pagina dove posso modificare l'autore
	 **/
	@GetMapping("/admin/editAutore")
	public String showAutoreEditPage(Model model, @RequestParam Long id) {

		try {
			// mi prendo l'autore corrispondente all'id
			Autore autore = autoreRepository.findById(id).get();
			model.addAttribute("autore", autore);

			// creo il nuovo autore transiente
			AutoreDTO dto = new AutoreDTO();
			dto.setNome(autore.getNome());
			dto.setCognome(autore.getCognome());
			dto.setNazionalita(autore.getNazionalita());
			dto.setAnnoNascita(autore.getAnnoNascita());
			dto.setAnnoMorte(autore.getAnnoMorte());

			model.addAttribute("autoreDTO", dto);

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "redirect:/admin/autori";
		}

		return "admin/editAutore.html";
	}

	/**
	 * metodo che gestisce cosa fare una volta modificato l'autore dato il suo id
	 * 
	 * @param modello come dati da passare per modificare i dati dell'autore
	 * @param id      dell'autore da modificare
	 * @param oggetto transiente da modificare
	 **/
	@PostMapping("/admin/editAutore")
	public String updateAutore(Model model, @RequestParam Long id, @Valid @ModelAttribute AutoreDTO dto,
			BindingResult result) {

		// mi accerto di essere connesso al database
		try {

			// prendo l'autore a partire dal suo id
			Autore autore = autoreRepository.findById(id).get();
			model.addAttribute("autore", autore);

			// verifico se i parametri del form sono validi
			if (result.hasErrors()) {
				return "admin/indexAdmin.html";
			}

			// se non ci sono errori aggiorno i valori
			autore.setNome(dto.getNome());
			autore.setCognome(dto.getCognome());
			autore.setNazionalita(dto.getNazionalita());
			autore.setAnnoNascita(dto.getAnnoNascita());
			autore.setAnnoMorte(dto.getAnnoMorte());

			autoreService.save(autore);

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}

		return "redirect:/admin/autori";
	}

	
	/**
	 * metodo per cancellare un autore dato il suo id
	 * 
	 * @param id dell'autore da cancellare
	 * @return autore cancellato
	 **/
	@GetMapping("/admin/deleteAutore")
	public String deleteAutore(@RequestParam Long id) {

		try {
			// prendo l'autore corrente (quello corrispondente all'id passato)
			Autore autore = autoreRepository.findById(id).get();

			// elimino l'oggetto autore vero e proprio
			autoreRepository.delete(autore);

		} catch (Exception ex) {
			System.out.println("Exception : " + ex.getMessage());
		}

		return "redirect:/admin/autori";
	}


}
