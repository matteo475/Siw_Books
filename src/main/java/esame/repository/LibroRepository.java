package esame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import esame.model.Libro;
import esame.model.Recensione;

public interface LibroRepository extends JpaRepository<Libro, Long> {
	public List<Libro> findByTitolo(String titolo);

	public Long countById(Long id);
	public List<Libro> findByTitoloContainingIgnoreCase(String titolo);

	public Optional<Libro> findById(Long id);

	@Query("SELECT l FROM Libro l " + " LEFT JOIN FETCH l.recensioni " + " LEFT JOIN FETCH l.autori "
			+ " WHERE l.id = :id")
	Optional<Libro> findByIdWithRecensioniAndAutori(@Param("id") Long id);
	
	
}
