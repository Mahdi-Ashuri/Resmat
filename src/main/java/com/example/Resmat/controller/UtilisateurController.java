package com.example.Resmat.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.securite.UtilisateurConnecte;
import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpSession;

// Accessible uniquement aux administrateurs (voir AuthInterceptor) :
// c'est ici que l'admin crée les comptes et décide des rôles.
@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final HttpSession session;

    public UtilisateurController(UtilisateurService utilisateurService, HttpSession session) {
        this.utilisateurService = utilisateurService;
        this.session = session;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.getAllUtilisateurs());
        return "utilisateurs/index";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        return afficherFormulaire(model, "Nouvel utilisateur", new Utilisateur());
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        if (utilisateur.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Utilisateur introuvable.");
            return "redirect:/utilisateurs";
        }
        return afficherFormulaire(model, "Modifier l'utilisateur", utilisateur.get());
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Utilisateur utilisateur, Model model,
                              RedirectAttributes redirectAttributes) {
        boolean nouveau = utilisateur.getIdUtilisateur() == 0;
        String titre = nouveau ? "Nouvel utilisateur" : "Modifier l'utilisateur";
        UtilisateurConnecte moi = UtilisateurConnecte.de(session);

        // Garde-fou : un admin ne peut pas se retirer lui-même le rôle ADMIN (il se bloquerait)
        if (!nouveau && moi != null && utilisateur.getIdUtilisateur() == moi.id()
                && !UtilisateurService.ROLE_ADMIN.equalsIgnoreCase(utilisateur.getRole())) {
            model.addAttribute("erreur", "Vous ne pouvez pas retirer votre propre rôle d'administrateur.");
            return afficherFormulaire(model, titre, utilisateur);
        }

        // Modification avec mot de passe laissé vide : on garde le mot de passe (déjà haché) actuel
        if (!nouveau && (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isBlank())) {
            utilisateurService.getUtilisateurById(utilisateur.getIdUtilisateur())
                    .ifPresent(existant -> utilisateur.setMotDePasse(existant.getMotDePasse()));
        }

        try {
            utilisateurService.saveUtilisateur(utilisateur);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur enregistré.");
            return "redirect:/utilisateurs";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            return afficherFormulaire(model, titre, utilisateur);
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        UtilisateurConnecte moi = UtilisateurConnecte.de(session);
        if (moi != null && id == moi.id()) {
            redirectAttributes.addFlashAttribute("erreur", "Vous ne pouvez pas supprimer votre propre compte.");
            return "redirect:/utilisateurs";
        }
        try {
            utilisateurService.deleteUtilisateur(id);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur supprimé.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    // ---------- méthodes privées ----------

    private String afficherFormulaire(Model model, String titre, Utilisateur utilisateur) {
        model.addAttribute("titre", titre);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("nouveau", utilisateur.getIdUtilisateur() == 0);
        model.addAttribute("rolesOptions", rolesOptions(utilisateur.getRole()));
        return "utilisateurs/form";
    }

    private List<Map<String, Object>> rolesOptions(String roleSelectionne) {
        return List.of(
                option(UtilisateurService.ROLE_ADMIN, "Administrateur", roleSelectionne),
                option(UtilisateurService.ROLE_GESTIONNAIRE, "Gestionnaire", roleSelectionne),
                option(UtilisateurService.ROLE_EMPRUNTEUR, "Emprunteur", roleSelectionne));
    }

    private static Map<String, Object> option(String role, String label, String roleSelectionne) {
        return Map.<String, Object>of("id", role, "label", label,
                "selected", role.equalsIgnoreCase(roleSelectionne));
    }
}