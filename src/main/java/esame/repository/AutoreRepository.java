package esame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import esame.model.*;

public interface AutoreRepository extends JpaRepository<Autore, Long>{
	public List<Autore> findByNome(String nome);
	public Long countById(Long id);
	public Optional<Autore> findById(Long id);
}
