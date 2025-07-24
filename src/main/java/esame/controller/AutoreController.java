package esame.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import esame.DTOmodel.RecensioneDTO;
import esame.model.Autore;
import esame.model.Immagine;
import esame.model.Libro;
import esame.repository.AutoreRepository;
import esame.repository.ImmagineRepository;
import esame.service.AutoreService;
import jakarta.persistence.EntityNotFoundException;

@Controller
public class AutoreController {

	@Autowired
	private ImmagineRepository immagineRepository;

	@Autowired
	private AutoreService autoreService;

	@Autowired
	private AutoreRepository autoreRepository;

	/**
	 * metodo che mi permette di vedere i dettagli sel libro
	 * @return pagina con dettagli libro
	 **/
	@GetMapping("/autore/{id}")
	public String showDettagli(@PathVariable Long id, Model model) {
		
		Autore autore = autoreService.findById(id);
	    model.addAttribute("autore", autore);
		return "/autore/showAutore.html"; // pagina web del libro
	}
	

	/**
	 * metodo per avere le immagini del libri 
	 * @return l'immagine specifica del libro 
	 **/
	@GetMapping("/autore/immagine/{iid}")
	public ResponseEntity<byte[]> serveImage(@PathVariable("iid") Long imageId) {

		Immagine img = immagineRepository.findById(imageId).orElseThrow(() -> new EntityNotFoundException("Immagine non trovata"));
		return ResponseEntity.ok().contentType(MediaType.valueOf(img.getTipoContenuto())).body(img.getDati());
	}

	/**
	 * metodo per visualizzare l'homepage del sito
	 * 
	 * @return index.html (homepage del sito)
	 */
	@GetMapping("/showAllAutori")
	public String showAllAutori(Model model) {
		List<Autore> autori = autoreRepository.findAll();
		model.addAttribute("autori", autori);
		return "tuttiAutori"; // Thymeleaf template: src/main/resources/templates/home.html
	}
	
	
}
