package esame.model;

import java.sql.Date;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "autore")
public class Autore {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
	private String cognome;
	private Date annoNascita;
	private Date annoMorte; // se morto, mettere controllo
	private String nazionalita;
	
	@Column(length = 2000)
	private String biografia;

	// Many-to-Many verso Autore
	@ManyToMany(mappedBy = "autori", fetch = FetchType.LAZY)
	private Set<Libro> libri = new HashSet<>();

	@OneToMany(mappedBy = "autore", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Immagine> immagini = new ArrayList<>();

	// metodi getter e setter
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
		this.nome = paroleMaiuscole(nome);
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = paroleMaiuscole(cognome);
	}
	
	/**
	 * metodo che serve per rendere maiuscola il primo carattere della
	 * parola
	 * @param Stringa di testo 
	 * @return la stringa con il primo carattere maiuscolo
	 * */
	private String paroleMaiuscole(String testo) {
		
		if(testo == null || testo.isBlank()) {
			return testo; 
		}
		
		String[] frase = testo.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i < frase.length; i++) {
			
			String w = frase[i];
			sb.append(w.substring(0,1).toUpperCase() + w.substring(1).toLowerCase());
			
			if(i < frase.length-1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
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

	public List<Immagine> getImmagini() {
		return immagini;
	}

	public void setImmagini(List<Immagine> immagini) {
		this.immagini = immagini;
	}

	// utilità per aggiungere/rimuovere
	public void addImmagineAutore(Immagine img) {
		immagini.add(img);
		img.setAutore(this);
	}

	public void removeImmagine(Immagine img) {
		immagini.remove(img);
		img.setAutore(this);
	}

	public Set<Libro> getLibri() {
		return libri;
	}

	public void setLibri(Set<Libro> libri) {
		this.libri = libri;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	// metodi hash e equals
	@Override
	public int hashCode() {
		return Objects.hash(annoMorte, annoNascita, cognome, id, nazionalita, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Autore other = (Autore) obj;
		return Objects.equals(annoMorte, other.annoMorte) && Objects.equals(annoNascita, other.annoNascita)
				&& Objects.equals(cognome, other.cognome) && Objects.equals(id, other.id)
				&& Objects.equals(nazionalita, other.nazionalita) && Objects.equals(nome, other.nome);
	}

}
