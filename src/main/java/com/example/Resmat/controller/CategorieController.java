package com.example.Resmat.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Resmat.entities.Categorie;
import com.example.Resmat.services.CategorieService;

@Controller
@RequestMapping("/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("categories", categorieService.getAllCategories());
        return "categories/index";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("titre", "Nouvelle catégorie");
        model.addAttribute("categorie", new Categorie());
        return "categories/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Categorie> categorie = categorieService.getCategorieById(id);
        if (categorie.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Catégorie introuvable.");
            return "redirect:/categories";
        }
        model.addAttribute("titre", "Modifier la catégorie");
        model.addAttribute("categorie", categorie.get());
        return "categories/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Categorie categorie, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            categorieService.saveCategorie(categorie);
            redirectAttributes.addFlashAttribute("succes", "Catégorie enregistrée.");
            return "redirect:/categories";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("titre", categorie.getIdCategorie() == 0
                    ? "Nouvelle catégorie" : "Modifier la catégorie");
            model.addAttribute("categorie", categorie);
            return "categories/form";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.deleteCategorie(id);
            redirectAttributes.addFlashAttribute("succes", "Catégorie supprimée.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/categories";
    }
}