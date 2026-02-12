package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.dto.UserAuthDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {


    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
       SELECT new br.com.jtech.tasklist.adapters.output.repositories.entities.dto.UserAuthDTO(
           u.id,
           u.email,
           u.name,
           u.password,
           u.active
       )
       FROM UserEntity u
       WHERE u.email = :email
       AND u.active = true
       """)
    Optional<UserAuthDTO> findActiveByEmail(String email);

    @Query("""
       SELECT new br.com.jtech.tasklist.adapters.output.repositories.entities.dto.UserAuthDTO(
           u.id,
           u.email,
           u.name,
           u.password,
           u.active
       )
       FROM UserEntity u
       WHERE u.id = :id
       AND u.active = true
       """)
    Optional<UserAuthDTO> findActiveById(UUID id);
}
