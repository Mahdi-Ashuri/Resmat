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
public class ProfilController {

    private final UtilisateurService utilisateurService;

    public ProfilController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/profil")
    public String afficher(HttpServletRequest request, Model model) {
        remplir(model, courant(request));
        return "profil/profil";
    }

    @PostMapping("/profil")
    public String modifierInfos(
            @RequestParam("prenom") String prenom,
            @RequestParam("nom") String nom,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        Utilisateur u = courant(request);
        try {
            utilisateurService.modifierProfil(u.getIdUtilisateur(), prenom, nom);
            redirectAttributes.addFlashAttribute("succes", "Profil mis à jour.");
            return "redirect:/profil";
        } catch (IllegalArgumentException | IllegalStateException e) {
            remplir(model, courant(request));
            model.addAttribute("prenom", prenom);   
            model.addAttribute("nom", nom);
            model.addAttribute("erreur", e.getMessage());
            return "profil/profil";
        }
    }

    @PostMapping("/profil/mot-de-passe")
    public String changerMotDePasse(
            @RequestParam("ancien") String ancien,
            @RequestParam("nouveau") String nouveau,
            @RequestParam("confirmation") String confirmation,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        Utilisateur u = courant(request);
        try {
            utilisateurService.changerMotDePasse(u.getIdUtilisateur(), ancien, nouveau, confirmation);
            redirectAttributes.addFlashAttribute("succes", "Mot de passe modifié.");
            return "redirect:/profil";
        } catch (IllegalArgumentException | IllegalStateException e) {
            remplir(model, u);
            model.addAttribute("erreur", e.getMessage());
            return "profil/profil";
        }
    }

    private Utilisateur courant(HttpServletRequest request) {
        UtilisateurConnecte moi = UtilisateurConnecte.de(request.getSession(false));
        return utilisateurService.getUtilisateurById(moi.id()).orElseThrow();
    }

    private void remplir(Model model, Utilisateur u) {
        model.addAttribute("prenom", u.getPrenom());
        model.addAttribute("nom", u.getNom());
        model.addAttribute("email", u.getEmail());
        model.addAttribute("login", u.getLogin());
        model.addAttribute("role", u.getRole());
    }
}