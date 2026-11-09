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

import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.services.CategorieService;
import com.example.Resmat.services.TypeMaterielService;

@Controller
@RequestMapping("/types-materiel")
public class TypeMaterielController {

    private final TypeMaterielService typeMaterielService;
    private final CategorieService categorieService;

    public TypeMaterielController(TypeMaterielService typeMaterielService,
                                  CategorieService categorieService) {
        this.typeMaterielService = typeMaterielService;
        this.categorieService = categorieService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("typesMateriel", typeMaterielService.getAllTypesMateriel());
        return "typesmateriel/index";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        TypeMateriel type = new TypeMateriel();
        type.setDureeMaxEmprunt(7);
        return afficherFormulaire(model, "Nouveau type de matériel", type);
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TypeMateriel> type = typeMaterielService.getTypeMaterielById(id);
        if (type.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Type de matériel introuvable.");
            return "redirect:/types-materiel";
        }
        return afficherFormulaire(model, "Modifier le type de matériel", type.get());
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute TypeMateriel typeMateriel, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            typeMaterielService.saveTypeMateriel(typeMateriel);
            redirectAttributes.addFlashAttribute("succes", "Type de matériel enregistré.");
            return "redirect:/types-materiel";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            return afficherFormulaire(model,
                    typeMateriel.getIdTypeMateriel() == 0 ? "Nouveau type de matériel" : "Modifier le type de matériel",
                    typeMateriel);
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            typeMaterielService.deleteTypeMateriel(id);
            redirectAttributes.addFlashAttribute("succes", "Type de matériel supprimé.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/types-materiel";
    }


    private String afficherFormulaire(Model model, String titre, TypeMateriel type) {
        Integer idCategorie = type.getCategorie() != null ? type.getCategorie().getIdCategorie() : null;
        model.addAttribute("titre", titre);
        model.addAttribute("typeMateriel", type);
        model.addAttribute("categoriesOptions", categoriesOptions(idCategorie));
        return "typesmateriel/form";
    }

    private List<Map<String, Object>> categoriesOptions(Integer idSelectionne) {
        return categorieService.getAllCategories().stream()
                .map(c -> option(c.getIdCategorie(), c.getNom(),
                        idSelectionne != null && idSelectionne == c.getIdCategorie()))
                .toList();
    }

    private static Map<String, Object> option(Object id, String label, boolean selected) {
        return Map.<String, Object>of("id", id, "label", label == null ? "" : label, "selected", selected);
    }
}