
package com.example.Resmat.services;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.repositories.UtilisateurRepositorie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepositorie utilisateurRepositorie;

    public UtilisateurService(UtilisateurRepositorie utilisateurRepositorie) {
        this.utilisateurRepositorie = utilisateurRepositorie;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepositorie.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Integer id) {
        return utilisateurRepositorie.findById(id);
    }

    public Optional<Utilisateur> getUtilisateurByLogin(String login) {
        return utilisateurRepositorie.findByLogin(login);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepositorie.findByEmail(email);
    }

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepositorie.save(utilisateur);
    }

    public void deleteUtilisateur(Integer id) {
        utilisateurRepositorie.deleteById(id);
    }

    public boolean loginExists(String login) {
        return utilisateurRepositorie.existsByLogin(login);
    }

    public boolean emailExists(String email) {
        return utilisateurRepositorie.existsByEmail(email);
    }
}
