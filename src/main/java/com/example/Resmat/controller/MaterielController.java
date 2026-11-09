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

import com.example.Resmat.entities.Materiel;
import com.example.Resmat.services.MaterielService;
import com.example.Resmat.services.TypeMaterielService;

@Controller
@RequestMapping("/materiels")
public class MaterielController {

    private final MaterielService materielService;
    private final TypeMaterielService typeMaterielService;

    public MaterielController(MaterielService materielService,
                              TypeMaterielService typeMaterielService) {
        this.materielService = materielService;
        this.typeMaterielService = typeMaterielService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("materiels", materielService.getAllMateriels());
        return "materiels/index";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        return afficherFormulaire(model, "Nouveau matériel", new Materiel());
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Materiel> materiel = materielService.getMaterielById(id);
        if (materiel.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Matériel introuvable.");
            return "redirect:/materiels";
        }
        return afficherFormulaire(model, "Modifier le matériel", materiel.get());
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Materiel materiel, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            materielService.saveMateriel(materiel);
            redirectAttributes.addFlashAttribute("succes", "Matériel enregistré.");
            return "redirect:/materiels";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            return afficherFormulaire(model,
                    materiel.getIdMateriel() == 0 ? "Nouveau matériel" : "Modifier le matériel",
                    materiel);
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            materielService.deleteMateriel(id);
            redirectAttributes.addFlashAttribute("succes", "Matériel supprimé.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/materiels";
    }


    private String afficherFormulaire(Model model, String titre, Materiel materiel) {
        Integer idType = materiel.getTypeMateriel() != null
                ? materiel.getTypeMateriel().getIdTypeMateriel() : null;
        model.addAttribute("titre", titre);
        model.addAttribute("materiel", materiel);
        model.addAttribute("typesOptions", typesOptions(idType));
        return "materiels/form";
    }

    private List<Map<String, Object>> typesOptions(Integer idSelectionne) {
        return typeMaterielService.getAllTypesMateriel().stream()
                .map(t -> option(t.getIdTypeMateriel(), t.getNom(),
                        idSelectionne != null && idSelectionne == t.getIdTypeMateriel()))
                .toList();
    }

    private static Map<String, Object> option(Object id, String label, boolean selected) {
        return Map.<String, Object>of("id", id, "label", label == null ? "" : label, "selected", selected);
    }
}