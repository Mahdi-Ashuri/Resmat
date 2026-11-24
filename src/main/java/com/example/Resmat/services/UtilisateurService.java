package com.example.Resmat.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.repositories.ReservationRepositorie;
import com.example.Resmat.repositories.UtilisateurRepositorie;

@Service
public class UtilisateurService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";
    public static final String ROLE_EMPRUNTEUR = "EMPRUNTEUR";

    private static final Set<String> ROLES_VALIDES =
            Set.of(ROLE_ADMIN, ROLE_GESTIONNAIRE, ROLE_EMPRUNTEUR);

    private final UtilisateurRepositorie utilisateurRepositorie;
    private final ReservationRepositorie reservationRepositorie;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UtilisateurService(UtilisateurRepositorie utilisateurRepositorie,
                              ReservationRepositorie reservationRepositorie) {
        this.utilisateurRepositorie = utilisateurRepositorie;
        this.reservationRepositorie = reservationRepositorie;
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

    public List<Utilisateur> getUtilisateursParRole(String role) {
        return utilisateurRepositorie.findByRole(role);
    }

    public boolean loginExists(String login) {
        return utilisateurRepositorie.existsByLogin(login);
    }

    public boolean emailExists(String email) {
        return utilisateurRepositorie.existsByEmail(email);
    }

    @Transactional
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        exigerTexte(utilisateur.getNom(), "Le nom est obligatoire.");
        exigerTexte(utilisateur.getPrenom(), "Le prénom est obligatoire.");
        exigerTexte(utilisateur.getEmail(), "L'email est obligatoire.");
        exigerTexte(utilisateur.getLogin(), "Le login est obligatoire.");
        exigerTexte(utilisateur.getMotDePasse(), "Le mot de passe est obligatoire.");
        exigerTexte(utilisateur.getRole(), "Le rôle est obligatoire.");

        utilisateur.setNom(utilisateur.getNom().trim());
        utilisateur.setPrenom(utilisateur.getPrenom().trim());
        utilisateur.setEmail(utilisateur.getEmail().trim());
        utilisateur.setLogin(utilisateur.getLogin().trim());

        String role = utilisateur.getRole().trim().toUpperCase();
        if (!ROLES_VALIDES.contains(role)) {
            throw new IllegalArgumentException("Rôle invalide (ADMIN, GESTIONNAIRE ou EMPRUNTEUR).");
        }
        utilisateur.setRole(role);

        int id = utilisateur.getIdUtilisateur();
        utilisateurRepositorie.findByLogin(utilisateur.getLogin())
                .filter(u -> u.getIdUtilisateur() != id)
                .ifPresent(u -> { throw new IllegalStateException("Ce login est déjà utilisé."); });
        utilisateurRepositorie.findByEmail(utilisateur.getEmail())
                .filter(u -> u.getIdUtilisateur() != id)
                .ifPresent(u -> { throw new IllegalStateException("Cet email est déjà utilisé."); });

        if (!estDejaHache(utilisateur.getMotDePasse())) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }

        return utilisateurRepositorie.save(utilisateur);
    }

    public Optional<Utilisateur> authentifier(String login, String motDePasseClair) {
        return utilisateurRepositorie.findByLogin(login)
                .filter(u -> passwordEncoder.matches(motDePasseClair, u.getMotDePasse()));
    }
    @Transactional
    public Utilisateur modifierProfil(Integer id, String prenom, String nom) {
        Utilisateur u = utilisateurRepositorie.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        u.setPrenom(prenom);
        u.setNom(nom);
        return saveUtilisateur(u); // valide (non vide), nettoie et sauvegarde
    }

    @Transactional
    public void changerMotDePasse(Integer id, String ancien, String nouveau, String confirmation) {
        Utilisateur u = utilisateurRepositorie.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (!passwordEncoder.matches(ancien, u.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        if (nouveau == null || nouveau.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }
        if (!nouveau.equals(confirmation)) {
            throw new IllegalArgumentException("Les deux mots de passe ne sont pas identiques.");
        }
        u.setMotDePasse(passwordEncoder.encode(nouveau));
        utilisateurRepositorie.save(u);
    }
    @Transactional
    public void deleteUtilisateur(Integer id) {
        if (!utilisateurRepositorie.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur introuvable.");
        }
        if (!reservationRepositorie.findByEmprunteurIdUtilisateur(id).isEmpty()
                || !reservationRepositorie.findByGestionnaireIdUtilisateur(id).isEmpty()) {
            throw new IllegalStateException(
                    "Suppression impossible : cet utilisateur est lié à des réservations.");
        }
        utilisateurRepositorie.deleteById(id);
    }

    private boolean estDejaHache(String mdp) {
        return mdp.length() == 60 && mdp.startsWith("$2");
    }

    private void exigerTexte(String valeur, String message) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}