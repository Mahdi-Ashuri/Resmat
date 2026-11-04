package com.example.Resmat.repositories;

import com.example.Resmat.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepositorie extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByLogin(String login);

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}
