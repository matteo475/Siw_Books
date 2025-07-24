package esame.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import esame.DTOmodel.RecensioneDTO;
import esame.model.Immagine;
import esame.model.Libro;
import esame.model.Recensione;
import esame.model.User;
import esame.repository.ImmagineRepository;
import esame.repository.LibroRepository;
import esame.repository.RecensioneRepository;
import esame.service.CredentialsService;
import esame.service.LibroService;
import esame.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class LibroController {

	@Autowired
	private LibroService libroService;

	@Autowired
	private UserService userService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ImmagineRepository immagineRepository;

	@Autowired
	private RecensioneRepository recensioneRepository;
	
	@Autowired
	private LibroRepository libroRepository;

	
	/*mi serve per far ritornare tutte le recensioni del dato libro*/
	public List<Recensione> getRecensioniDi(Long libroId) {
		return recensioneRepository.findByLibroId(libroId);
	}

	/**
	 * metodo che mi permette di vedere i dettagli sel libro
	 * @return pagina con dettagli libro
	 **/
	@GetMapping("/libro/{id}")
	public String showDettagli(@PathVariable Long id, Model model) {

		// recupero il libro
		Libro libro = libroService.caricaConRecensioni(id);
		model.addAttribute("libro", libro);

		// prepare un oggetto vuoto per il binding del form
		RecensioneDTO recensioneVuota = new RecensioneDTO();
		model.addAttribute("nuovaRecensione", recensioneVuota);

		return "/libro/showLibro.html"; // pagina web del libro
	}

	
	/**
	 * metodo per riprendere le immagini relative al libro
	 * @return immagine relativa al libro
	 * */
	@GetMapping("/libro/immagine/{iid}")
	public ResponseEntity<byte[]> serveImage(@PathVariable("iid") Long imageId) {

		Immagine img = immagineRepository.findById(imageId)
				.orElseThrow(() -> new EntityNotFoundException("Immagine non trovata"));

		return ResponseEntity.ok().contentType(MediaType.valueOf(img.getTipoContenuto())).body(img.getDati());
	}
	
	/**
	 * metodo per visualizzare l'homepage del sito
	 * 
	 * @return index.html (homepage del sito)
	 */
	@GetMapping("/showAllLibri")
	public String showAllLibro(Model model) {
		List<Libro> libri = libroRepository.findAll();
		model.addAttribute("libri", libri);
		return "tuttiLibri"; // Thymeleaf template: src/main/resources/templates/home.html
	}

}
