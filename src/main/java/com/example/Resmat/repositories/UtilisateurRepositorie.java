package com.example.Resmat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Resmat.entities.Utilisateur;

public interface UtilisateurRepositorie extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByLogin(String login);

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    List<Utilisateur> findByRole(String role);
}