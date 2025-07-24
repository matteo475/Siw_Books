package esame.controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import esame.DTOmodel.RecensioneDTO;
import esame.model.Credentials;
import esame.model.Libro;
import esame.model.Recensione;
import esame.model.User;
import esame.repository.RecensioneRepository;
import esame.service.CredentialsService;
import esame.service.LibroService;
import esame.service.RecensioneService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/* classe che gestisce le interazioni tre 
 * 	
 * 	UTENTE -----> RECENSIONE	
 *
 **/
@Controller
public class UtenteToRecensioneController {

	@Autowired
	private LibroService libroService;

	@Autowired
	private RecensioneService recensioneService;

	@Autowired
	private RecensioneRepository recensioneRepository;
	
	@Autowired
	private CredentialsService credentialsService;

	/**
	 * metodo che gestisce il POST del form di recensione
	 **/
	@PostMapping("/libro/{id}/recensione")
	public String saveRecensione(@PathVariable Long id, @ModelAttribute("nuovaRecensione") @Valid RecensioneDTO dto,
			BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttrs, Model model) {

		// verifico se ci sono errori di validazione
		if (bindingResult.hasErrors()) {
			Libro libro = libroService.caricaConRecensioni(id);
			model.addAttribute("libro", libro);
			return "libro/showLibro.html";
		}

		// funzione che gestisce l'inserimento della recensione nel libro
		recensioneService.aggiungiRecensione(id, dto, principal.getName());

		redirectAttrs.addFlashAttribute("success", "Grazie per la tua recensione!");

		// ricarico la pagina per mostrare anche la nuova recensione
		return "redirect:/libro/" + id;
	}

	/**
	 * metodo che gestisce la cancellazione da parte dell'utente della recensione
	 **/
	@GetMapping("/libro/{id}/recensione/delete")
	public String deleteRecensione(@RequestParam Long id) {

		try {

			// prendo la recensione corrente
			// Recensione recensione = recensioneRepository.findById(id);

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		return "redirect:/";
	}

	/**
	 * metodo per gestire la rimozione della recensione dell'utente
	 * 
	 * @return la pagina del libro senza la recensione
	 */
	/*
	 * @PostMapping("/libro/{libroId}/recensione/{recensioneId}/delete") public
	 * String deleteUserReview(@PathVariable Long libroId, @PathVariable Long
	 * recensioneId, Principal principal, RedirectAttributes redirectAttrs) {
	 * 
	 * try { // principal.getName() deve restituire il cognome con cui hai fatto il
	 * lookup recensioneService.deleteRecensioneIfOwner(recensioneId,
	 * principal.getName());
	 * 
	 * redirectAttrs.addFlashAttribute("success",
	 * "Recensione eliminata con successo");
	 * 
	 * } catch (EntityNotFoundException e) {
	 * redirectAttrs.addFlashAttribute("error", "Recensione non trovata");
	 * 
	 * } catch (AccessDeniedException e) { redirectAttrs.addFlashAttribute("error",
	 * "Non puoi cancellare recensioni di altri utenti"); }
	 * 
	 * return "redirect:/libro/" + libroId; }
	 */

	@PostMapping("/libro/{libroId}/recensione/{recensioneId}/delete")
	public String deleteUserReview(
	        @PathVariable Long libroId,
	        @PathVariable Long recensioneId,
	        Authentication authentication,
	        RedirectAttributes redirectAttrs) {

	    // estraggo lo username dal principal
	    Object principalObj = authentication.getPrincipal();
	    String username = (principalObj instanceof UserDetails)
	        ? ((UserDetails) principalObj).getUsername()
	        : principalObj.toString();

	    // recupero le credenziali dal DB
	    Credentials creds = credentialsService.getCredentials(username);
	    if (creds == null || creds.getUser() == null) {
	        return "redirect:/login?error";
	    }

	    // confronto diretto sul campo role
	    boolean isAdmin = Credentials.ADMIN_ROLE.equals(creds.getRole());

	    try {
	        if (isAdmin) {
	            // Admin: cancella senza controlli di ownership
	            recensioneService.deleteRecensione(recensioneId);
	        } else {
	            // Utente normale: può solo cancellare la propria recensione
	            recensioneService.deleteRecensioneIfOwner(recensioneId, username);
	        }
	        redirectAttrs.addFlashAttribute("success", "Recensione eliminata con successo");
	    } catch (EntityNotFoundException e) {
	        redirectAttrs.addFlashAttribute("error", "Recensione non trovata");
	    } catch (AccessDeniedException e) {
	        redirectAttrs.addFlashAttribute("error", "Non puoi cancellare recensioni di altri utenti");
	    }

	    return "redirect:/libro/" + libroId;
	}

}
