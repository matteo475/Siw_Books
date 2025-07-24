package esame.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import esame.model.*;

public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findBySurname(String username);
}
