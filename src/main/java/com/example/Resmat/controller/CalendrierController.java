package com.example.Resmat.controller;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.services.CalendrierService;
import com.example.Resmat.services.TypeMaterielService;

@Controller
@RequestMapping("/calendrier")
public class CalendrierController {

    private final CalendrierService calendrierService;
    private final TypeMaterielService typeMaterielService;

    public CalendrierController(CalendrierService calendrierService,
                                TypeMaterielService typeMaterielService) {
        this.calendrierService = calendrierService;
        this.typeMaterielService = typeMaterielService;
    }

    @GetMapping
    public String afficher(@RequestParam(name = "type", required = false) Integer idType,
                           @RequestParam(name = "mois", required = false) String moisTexte,
                           Model model) {

        List<TypeMateriel> types = typeMaterielService.getTypesAuCatalogue();
        model.addAttribute("aucunType", types.isEmpty());
        if (types.isEmpty()) {
            return "calendrier/index";
        }

        // Le type demandé s'il est au catalogue, sinon le premier de la liste
        TypeMateriel type = types.stream()
                .filter(t -> idType != null && t.getIdTypeMateriel() == idType)
                .findFirst()
                .orElse(types.get(0));

        YearMonth mois = lireMois(moisTexte);
        int capacite = calendrierService.capacite(type.getIdTypeMateriel());

        model.addAttribute("typesOptions", types.stream()
                .map(t -> option(t.getIdTypeMateriel(), t.getNom(),
                        t.getIdTypeMateriel() == type.getIdTypeMateriel()))
                .toList());
        model.addAttribute("idType", type.getIdTypeMateriel());
        model.addAttribute("nomType", type.getNom());
        model.addAttribute("dureeMax", type.getDureeMaxEmprunt());
        model.addAttribute("capacite", capacite);
        model.addAttribute("aucunExemplaire", capacite == 0);

        model.addAttribute("libelleMois", libelle(mois));
        model.addAttribute("moisCourant", mois.toString());                  // 2026-11
        model.addAttribute("moisPrecedent", mois.minusMonths(1).toString());
        model.addAttribute("moisSuivant", mois.plusMonths(1).toString());
        model.addAttribute("semaines", calendrierService.construireSemaines(type.getIdTypeMateriel(), mois));
        return "calendrier/index";
    }

    // "2026-11" → YearMonth ; toute valeur invalide → mois courant
    private static YearMonth lireMois(String texte) {
        try {
            return texte == null || texte.isBlank() ? YearMonth.now() : YearMonth.parse(texte.trim());
        } catch (DateTimeParseException e) {
            return YearMonth.now();
        }
    }

    private static String libelle(YearMonth mois) {
        String nom = mois.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return Character.toUpperCase(nom.charAt(0)) + nom.substring(1) + " " + mois.getYear();
    }

    private static Map<String, Object> option(Object id, String label, boolean selected) {
        return Map.<String, Object>of("id", id, "label", label == null ? "" : label, "selected", selected);
    }
}