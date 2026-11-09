package com.example.Resmat.securite;

import java.io.Serializable;

import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpSession;


public record UtilisateurConnecte(int id, String nom, String role) implements Serializable {

    public static final String SESSION_KEY = "utilisateurConnecte";

    public static UtilisateurConnecte de(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object valeur = session.getAttribute(SESSION_KEY);
        return valeur instanceof UtilisateurConnecte u ? u : null;
    }

    public boolean estAdmin() {
        return UtilisateurService.ROLE_ADMIN.equals(role);
    }

    public boolean estGestionnaire() {
        return UtilisateurService.ROLE_GESTIONNAIRE.equals(role);
    }

    public boolean estEmprunteur() {
        return UtilisateurService.ROLE_EMPRUNTEUR.equals(role);
    }

    public boolean peutGerer() {
        return estAdmin() || estGestionnaire();
    }
}