package com.example.Resmat.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Resmat.entities.Emprunt;
import com.example.Resmat.entities.Reservation;
import com.example.Resmat.services.EmpruntService;
import com.example.Resmat.services.MaterielService;
import com.example.Resmat.services.ReservationService;

@Controller
@RequestMapping("/emprunts")
public class EmpruntController {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EmpruntService empruntService;
    private final ReservationService reservationService;
    private final MaterielService materielService;

    public EmpruntController(EmpruntService empruntService,
                             ReservationService reservationService,
                             MaterielService materielService) {
        this.empruntService = empruntService;
        this.reservationService = reservationService;
        this.materielService = materielService;
    }


    @GetMapping
    public String liste(@RequestParam(name = "statut", required = false) String statut, Model model) {
        List<Emprunt> emprunts;
        if ("encours".equals(statut)) {
            emprunts = empruntService.getEmpruntsNonRetournes();
        } else if ("degrades".equals(statut)) {
            emprunts = empruntService.getEmpruntsAvecDegradation();
        } else {
            emprunts = empruntService.getAllEmprunts();
        }
        model.addAttribute("emprunts", emprunts);
        return "emprunts/index";
    }


    @GetMapping("/remise")
    public String remise(Model model) {
        model.addAttribute("reservationsOptions", reservationsOptions());
        model.addAttribute("materielsOptions", materielsOptions());
        return "emprunts/remise";
    }

    @PostMapping("/remise")
    public String remiseEnregistrer(@RequestParam("idReservation") Integer idReservation,
                                    @RequestParam("idMateriel") Integer idMateriel,
                                    @RequestParam(name = "commentaires", required = false) String commentaires,
                                    RedirectAttributes redirectAttributes) {
        try {
            empruntService.enregistrerRemise(idReservation, idMateriel, commentaires);
            redirectAttributes.addFlashAttribute("succes", "Remise du matériel enregistrée.");
            return "redirect:/emprunts";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/emprunts/remise";
        }
    }


    @GetMapping("/{id}/retour")
    public String retour(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Emprunt> emprunt = empruntService.getEmpruntById(id);
        if (emprunt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Emprunt introuvable.");
            return "redirect:/emprunts";
        }
        if (emprunt.get().getDateRetourEffective() != null) {
            redirectAttributes.addFlashAttribute("erreur", "Ce matériel a déjà été rendu.");
            return "redirect:/emprunts";
        }
        model.addAttribute("emprunt", emprunt.get());
        return "emprunts/retour";
    }

    @PostMapping("/{id}/retour")
    public String retourEnregistrer(@PathVariable("id") Integer id,
                                    @RequestParam(name = "degradation", defaultValue = "false") boolean degradation,
                                    @RequestParam(name = "commentaires", required = false) String commentaires,
                                    RedirectAttributes redirectAttributes) {
        try {
            empruntService.enregistrerRetour(id, degradation, commentaires);
            redirectAttributes.addFlashAttribute("succes", degradation
                    ? "Retour enregistré : le matériel est signalé comme dégradé."
                    : "Retour enregistré.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/emprunts";
    }


    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            empruntService.deleteEmprunt(id);
            redirectAttributes.addFlashAttribute("succes", "Emprunt supprimé.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/emprunts";
    }

    private List<Map<String, Object>> reservationsOptions() {
        return reservationService.getReservationsAccepteesSansEmprunt().stream()
                .map(r -> option(r.getIdReservation(), libelleReservation(r)))
                .toList();
    }

    private List<Map<String, Object>> materielsOptions() {
        return materielService.getMaterielsDisponibles().stream()
                .map(m -> option(m.getIdMateriel(),
                        m.getNom() + " (" + m.getTypeMateriel().getNom() + ")"))
                .toList();
    }

    private String libelleReservation(Reservation r) {
        return "#" + r.getIdReservation()
                + " - " + r.getEmprunteur().getPrenom() + " " + r.getEmprunteur().getNom()
                + " - " + r.getTypeMateriel().getNom()
                + " (du " + r.getDateDebut().format(FORMAT_DATE)
                + " au " + r.getDateFin().format(FORMAT_DATE) + ")";
    }

    private static Map<String, Object> option(Object id, String label) {
        return Map.<String, Object>of("id", id, "label", label == null ? "" : label, "selected", false);
    }
}