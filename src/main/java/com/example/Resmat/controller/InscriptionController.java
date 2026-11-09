
package com.example.Resmat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.securite.UtilisateurConnecte;
import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class InscriptionController {

    private final UtilisateurService utilisateurService;

    public InscriptionController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/inscription")
    public String afficher(HttpServletRequest request, Model model) {

        if (UtilisateurConnecte.de(request.getSession(false)) != null) {
            return "redirect:/";
        }

        model.addAttribute("prenom", "");
        model.addAttribute("nom", "");
        model.addAttribute("email", "");
        model.addAttribute("login", "");

        return "connexion/inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(
            @RequestParam("prenom") String prenom,
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("login") String login,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("confirmation") String confirmation,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (UtilisateurConnecte.de(request.getSession(false)) != null) {
            return "redirect:/";
        }

        try {

            if (!motDePasse.equals(confirmation)) {
                throw new IllegalArgumentException(
                        "Les deux mots de passe ne sont pas identiques.");
            }

            if (motDePasse.length() < 8) {
                throw new IllegalArgumentException(
                        "Le mot de passe doit contenir au moins 8 caractères.");
            }

            Utilisateur utilisateur = new Utilisateur();

            utilisateur.setPrenom(prenom.trim());
            utilisateur.setNom(nom.trim());
            utilisateur.setEmail(email.trim());
            utilisateur.setLogin(login.trim());
            utilisateur.setMotDePasse(motDePasse);

            utilisateur.setRole(UtilisateurService.ROLE_EMPRUNTEUR);

            utilisateurService.saveUtilisateur(utilisateur);

            redirectAttributes.addFlashAttribute(
                    "succes",
                    "Compte créé. Vous pouvez vous connecter.");

            return "redirect:/connexion";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("erreur", e.getMessage());

            model.addAttribute("prenom", prenom);
            model.addAttribute("nom", nom);
            model.addAttribute("email", email);
            model.addAttribute("login", login);

            return "inscription/inscription";
        }
    }
}
