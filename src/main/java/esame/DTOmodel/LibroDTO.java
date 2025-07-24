package esame.DTOmodel;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import esame.model.Libro;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class LibroDTO {

	// variabili di istanza
	private Long id;

	@NotBlank(message = "Il campo Titolo non può essere vuoto")
	private String titolo;

	@NotNull(message = "La data di pubblicazione è obbligatoria")
	@PastOrPresent(message = "La data non può essere futura")
	private Date annoPubblicazione;

	@NotBlank(message = "Il campo Trama non può essere vuoto")
	@Size(max = 5000, message = "La trama non può superare i 500 caratteri")
	private String trama;

	@NotBlank(message = "Il genere è obbligatorio")
	private String genere;

	// qui aggiungiamo il file (puoi anche usare MultipartFile[] se servono più
	// immagini)
	@NotNull(message = "Devi caricare almeno un’immagine")
	private MultipartFile[] files;

	// mettere collegamento a autore
	@NotEmpty(message = "Devi selezionare almeno un autore")
	private List<Long> autoreId = new ArrayList<>();

	// costruttori
	public LibroDTO() {
	}

	public LibroDTO(Long id, String titolo, Date annoPubblicazione, String trama, String genere) {
		this.id = id;
		this.titolo = titolo;
		this.annoPubblicazione = annoPubblicazione;
		this.trama = trama;
		this.genere = genere;
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

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public Date getAnnoPubblicazione() {
		return annoPubblicazione;
	}

	public void setAnnoPubblicazione(Date annoPubblicazione) {
		this.annoPubblicazione = annoPubblicazione;
	}

	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] file) {
		this.files = file;
	}

	public List<Long> getAutoreId() {
		return autoreId;
	}

	public void setAutoreId(List<Long> autoreId) {
		this.autoreId = autoreId;
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

	/**
	 * metodo che crea un DTO a partire da un Libro
	 * 
	 * @param oggetto Libro
	 * @retun oggetto LibroDTO ( oggetto transiente )
	 **/
	public static LibroDTO fromEntity(Libro l) {
		// da modificare quando aggiungo l'immagine e la recensione
		return new LibroDTO(l.getId(), l.getTitolo(), l.getAnnoPubblicazione(), l.getTrama(), l.getGenere());
	}

	/**
	 * metodo che converte questa DTO in una entità Libro
	 * 
	 * @return entità Libro persistente
	 **/
	public Libro toEntity() {
		Libro l = new Libro();
		l.setTitolo(this.titolo);
		l.setAnnoPubblicazione(this.annoPubblicazione);
		l.setGenere(this.genere);
		l.setTrama(this.trama);
		return l;
	}
}
