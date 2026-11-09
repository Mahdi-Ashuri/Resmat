package com.example.Resmat.configurations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.services.UtilisateurService;

// Au démarrage : s'il n'existe aucun administrateur, on en crée un pour pouvoir se connecter.
// Le mot de passe est haché par UtilisateurService (BCrypt). À changer après la première connexion.
@Component
public class DonneesInitiales implements CommandLineRunner {

    private static final String LOGIN_ADMIN = "admin";
    private static final String MOT_DE_PASSE_ADMIN = "Admin123!";

    private final UtilisateurService utilisateurService;

    public DonneesInitiales(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public void run(String... args) {
        if (!utilisateurService.getUtilisateursParRole(UtilisateurService.ROLE_ADMIN).isEmpty()) {
            return;
        }
        if (utilisateurService.loginExists(LOGIN_ADMIN)) {
            System.out.println("[Resmat] Aucun administrateur, et le login '" + LOGIN_ADMIN
                    + "' est déjà pris : crée ou modifie un compte ADMIN dans la base.");
            return;
        }

        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("Resmat");
        admin.setEmail("admin@resmat.local");
        admin.setLogin(LOGIN_ADMIN);
        admin.setMotDePasse(MOT_DE_PASSE_ADMIN);
        admin.setRole(UtilisateurService.ROLE_ADMIN);
        utilisateurService.saveUtilisateur(admin);

        System.out.println("[Resmat] Administrateur créé : login '" + LOGIN_ADMIN
                + "' / mot de passe '" + MOT_DE_PASSE_ADMIN + "' (à changer).");
    }
}