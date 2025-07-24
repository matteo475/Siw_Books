package esame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.*;

import esame.service.LibroService;
import esame.model.*;
import esame.repository.LibroRepository;

@Controller
public class MainController {

	/*
	 * @GetMapping("/") public String showHomePage() { return "index.html"; }
	 */
	@Autowired
	private LibroService libroService;

	@Autowired
	private LibroRepository libroRepository;

	/**
	 * metodo per visualizzare l'homepage del sito
	 * 
	 * @return index.html (homepage del sito)
	 */
	@GetMapping("/")
	public String showHomePage(Model model) {
		List<Libro> libri = libroRepository.findAll();
		model.addAttribute("libri", libri);
		return "index"; // Thymeleaf template: src/main/resources/templates/home.html
	}

	@PostMapping("/ricercaHome")
	public String ricercaHome(Model model,@RequestParam("keyword") String keyword) {

		List<Libro> risultati = libroService.findByTitolo(keyword);
		model.addAttribute("libri", risultati);
		model.addAttribute("keyword", keyword);
		return "ricerca";
	}

}
