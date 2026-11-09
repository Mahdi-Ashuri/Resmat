package com.example.Resmat.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.securite.UtilisateurConnecte;
import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConnexionController {

    private final UtilisateurService utilisateurService;

    public ConnexionController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/connexion")
    public String afficher(
            @RequestParam(name = "deconnecte", required = false) String deconnecte,
            HttpServletRequest request,
            Model model) {

        if (UtilisateurConnecte.de(request.getSession(false)) != null) {
            return "redirect:/";
        }

        model.addAttribute("login", "");

        if (deconnecte != null) {
            model.addAttribute("succes", "Vous avez été déconnecté.");
        }

        return "connexion/connexion";
    }

    @PostMapping("/connexion")
    public String connecter(
            @RequestParam("login") String login,
            @RequestParam("motDePasse") String motDePasse,
            HttpServletRequest request,
            Model model) {

        Optional<Utilisateur> utilisateur =
                utilisateurService.authentifier(login.trim(), motDePasse);

        if (utilisateur.isEmpty()) {
            model.addAttribute("erreur",
                    "Login ou mot de passe incorrect.");

            model.addAttribute("login", login);

            return "connexion/connexion";
        }

        Utilisateur u = utilisateur.get();

        HttpSession session = request.getSession();

        request.changeSessionId();

        session.setAttribute(
                UtilisateurConnecte.SESSION_KEY,
                new UtilisateurConnecte(
                        u.getIdUtilisateur(),
                        u.getPrenom() + " " + u.getNom(),
                        u.getRole()
                )
        );

        return "redirect:/";
    }

    @PostMapping("/deconnexion")
    public String deconnecter(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/connexion?deconnecte";
    }
}
