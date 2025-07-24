package esame.DTOmodel;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import esame.model.Autore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AutoreDTO {

	private Long id; 
	private String nome; 
	private String cognome; 
	private Date annoNascita; 
	private Date annoMorte; //se morto, mettere controllo
	private String nazionalita;
	
	@Size(max = 5000, message = "La trama non può superare i 500 caratteri")
	@Column(length = 20000)
	private String biografia;

	@NotNull(message = "Devi caricare almeno un’immagine")
	private MultipartFile[] files;

	
	//costruttori 
    public AutoreDTO() {}
    
    public AutoreDTO(Long id, String nome, String cognome, Date annoNascita, Date annoMorte, String nazionalita,String biografia) {
    	this.id = id; 
    	this.nome = nome; 
    	this.cognome = cognome; 
    	this.annoNascita = annoNascita;
    	this.annoMorte = annoMorte;
    	this.nazionalita = nazionalita;
    	this.biografia = biografia;
    }
	
	
	//metodi getter e setter
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public Date getAnnoNascita() {
		return annoNascita;
	}
	public void setAnnoNascita(Date annoNascita) {
		this.annoNascita = annoNascita;
	}
	public Date getAnnoMorte() {
		return annoMorte;
	}
	public void setAnnoMorte(Date annoMorte) {
		this.annoMorte = annoMorte;
	}
	public String getNazionalita() {
		return nazionalita;
	}
	public void setNazionalita(String nazionalita) {
		this.nazionalita = nazionalita;
	}
	
	//mettere immagine o collegamento a entitita 
	//mettere recensione o collegamento a recensione
	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	/**
	 * metodo che crea un DTO a partire da un Autore
	 **/
	public static AutoreDTO fromEntity(Autore a) {
		
		//da modificare quando aggiungo l'immagine e la recensione 
		return new AutoreDTO(a.getId(),a.getNome(),a.getCognome(),a.getAnnoNascita(),a.getAnnoNascita(),a.getNazionalita(),a.getBiografia());
	}
	
	/**
	 * metoco che converte questa DTO in entià Autore 
	 **/
	public Autore toEntity() {
		Autore a = new Autore();
		a.setAnnoNascita(this.annoNascita);
		a.setAnnoMorte(this.annoMorte);
		a.setCognome(this.cognome);
		a.setNome(this.nome);
		a.setNazionalita(this.nazionalita);
		a.setBiografia(this.biografia);
		//righe da aggiungere quando metto le immagini e le recensioni
		return a;
	}
	
	
}
