package esame.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
public class Libro {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String titolo;
	private Date annoPubblicazione;
	private String genere;
	
	@Column(length = 2000)
	private String trama;	
	

	@OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Recensione> recensioni = new ArrayList<>();

	@OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Immagine> immagini = new ArrayList<>();

	// Many-to-Many verso Autore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "libro_autore", joinColumns = @JoinColumn(name = "libro_id"), inverseJoinColumns = @JoinColumn(name = "autore_id"))
	private Set<Autore> autori = new HashSet<>();

	// getter e setter
	public List<Immagine> getImmagini() {
		return immagini;
	}

	public void setImmagini(List<Immagine> immagini) {
		this.immagini = immagini;
	}

	// utilità per aggiungere/rimuovere
	public void addImmagine(Immagine img) {
		immagini.add(img);
		img.setLibro(this);
	}

	public void removeImmagine(Immagine img) {
		immagini.remove(img);
		img.setLibro(null);
	}

	// metodi getter e setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	/*
	 * VECCHIO SETTER PER IL TITOLO DEL LIBRO public void setTitolo(String titolo) {
	 * this.titolo = titolo; }
	 */

	public void setTitolo(String titolo) {
		this.titolo = paroleMaiuscole(titolo);
	}

	/**
	 * metodo che serve per rendere maiuscola il primo carattere della parola
	 * 
	 * @param Stringa di testo
	 * @return la stringa con il primo carattere maiuscolo
	 */
	private String paroleMaiuscole(String testo) {

		if (testo == null || testo.isBlank()) {
			return testo;
		}

		String[] frase = testo.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < frase.length; i++) {

			String w = frase[i];
			sb.append(w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase());

			if (i < frase.length - 1) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	public Date getAnnoPubblicazione() {
		return annoPubblicazione;
	}

	public void setAnnoPubblicazione(Date annoPubblicazione) {
		this.annoPubblicazione = annoPubblicazione;
	}

	public List<Recensione> getRecensioni() {
		return this.recensioni;
	}

	public void setRecensioni(List<Recensione> recensione) {
		this.recensioni = recensione;
	}

	public Set<Autore> getAutori() {
		return autori;
	}

	public void setAutori(Set<Autore> autori) {
		this.autori = autori;
	}

	// helper per add/remove
	public void addAutore(Autore a) {
		autori.add(a);
		a.getLibri().add(this);
	}

	public void removeAutore(Autore a) {
		autori.remove(a);
		a.getLibri().remove(this);
	}

	public String getTrama() {
		return trama;
	}

	public void setTrama(String trama) {
		this.trama = trama;
	}

	public String getGenere() {
		return genere;
	}

	public void setGenere(String genere) {
		this.genere = genere;
	}

	// metodi hash e equals
	@Override
	public int hashCode() {
		return Objects.hash(annoPubblicazione, id, titolo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Libro other = (Libro) obj;
		return Objects.equals(annoPubblicazione, other.annoPubblicazione) && Objects.equals(id, other.id)
				&& Objects.equals(titolo, other.titolo);
	}
}
