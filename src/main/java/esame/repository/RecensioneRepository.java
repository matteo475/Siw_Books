package esame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import esame.model.*;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {
	List<Recensione> findByLibroId(Long libroId);

	@Modifying
	@Query("DELETE FROM Recensione r WHERE r.libro.id = :libroId AND r.user.id = :userId")
	void deleteByLibroAndUtente(@Param("libroId") Long libroId, @Param("userId") Long utenteId);
}
