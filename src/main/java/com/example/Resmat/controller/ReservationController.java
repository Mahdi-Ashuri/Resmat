package com.example.Resmat.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Resmat.entities.Reservation;
import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.securite.UtilisateurConnecte;
import com.example.Resmat.services.ReservationService;
import com.example.Resmat.services.TypeMaterielService;
import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final TypeMaterielService typeMaterielService;
    private final UtilisateurService utilisateurService;
    private final HttpSession session;

    public ReservationController(ReservationService reservationService,
                                 TypeMaterielService typeMaterielService,
                                 UtilisateurService utilisateurService,
                                 HttpSession session) {
        this.reservationService = reservationService;
        this.typeMaterielService = typeMaterielService;
        this.utilisateurService = utilisateurService;
        this.session = session;
    }

    // Les champs <input type="datetime-local"> envoient "2026-11-09T14:30"
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String texte) {
                if (texte == null || texte.isBlank()) {
                    setValue(null);
                } else {
                    setValue(LocalDateTime.parse(texte.trim()));
                }
            }
        });
    }

    // ---------- Liste (filtre : ?statut=attente | acceptees | refusees) ----------

    @GetMapping
    public String liste(@RequestParam(name = "statut", required = false) String statut, Model model) {
        List<Reservation> reservations;
        if ("attente".equals(statut)) {
            reservations = reservationService.getReservationsEnAttente();
        } else if ("acceptees".equals(statut)) {
            reservations = reservationService.getReservationsAcceptees();
        } else if ("refusees".equals(statut)) {
            reservations = reservationService.getReservationsRefusees();
        } else {
            reservations = reservationService.getAllReservations();
        }

        // Un emprunteur ne voit que ses propres réservations
        if (estEmprunteur()) {
            int idMoi = moi().id();
            reservations = reservations.stream()
                    .filter(r -> r.getEmprunteur().getIdUtilisateur() == idMoi)
                    .toList();
        }

        model.addAttribute("reservations", reservations);
        return "reservations/index";
    }

    // ---------- Création / modification ----------

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        return afficherFormulaire(model, "Nouvelle réservation", new Reservation());
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Réservation introuvable.");
            return "redirect:/reservations";
        }
        if (!estProprietaire(reservation.get())) {
            redirectAttributes.addFlashAttribute("erreur", "Cette réservation ne vous appartient pas.");
            return "redirect:/reservations";
        }
        if (reservation.get().getAvis() != null) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Une réservation déjà traitée ne peut plus être modifiée.");
            return "redirect:/reservations";
        }
        return afficherFormulaire(model, "Modifier la réservation", reservation.get());
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Reservation reservation, Model model,
                              RedirectAttributes redirectAttributes) {

        // Un emprunteur ne peut réserver que pour lui-même, et ne modifie que ses réservations
        if (estEmprunteur()) {
            if (reservation.getIdReservation() != 0) {
                Optional<Reservation> existante =
                        reservationService.getReservationById(reservation.getIdReservation());
                if (existante.isEmpty() || !estProprietaire(existante.get())) {
                    redirectAttributes.addFlashAttribute("erreur", "Cette réservation ne vous appartient pas.");
                    return "redirect:/reservations";
                }
            }
            Utilisateur proprietaire = new Utilisateur();
            proprietaire.setIdUtilisateur(moi().id());
            reservation.setEmprunteur(proprietaire);
        }

        try {
            reservationService.saveReservation(reservation);
            redirectAttributes.addFlashAttribute("succes", "Réservation enregistrée.");
            return "redirect:/reservations";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("erreur", e.getMessage());
            return afficherFormulaire(model,
                    reservation.getIdReservation() == 0 ? "Nouvelle réservation" : "Modifier la réservation",
                    reservation);
        }
    }

    // ---------- Traitement par un gestionnaire (réservé ADMIN / GESTIONNAIRE par l'intercepteur) ----------

    @GetMapping("/{id}/traiter")
    public String traiter(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Réservation introuvable.");
            return "redirect:/reservations";
        }
        if (reservation.get().getAvis() != null) {
            redirectAttributes.addFlashAttribute("erreur", "Cette réservation a déjà été traitée.");
            return "redirect:/reservations";
        }
        model.addAttribute("reservation", reservation.get());
        model.addAttribute("gestionnairesOptions", gestionnairesOptions(moi().id()));
        return "reservations/traiter";
    }

    @PostMapping("/{id}/traiter")
    public String traiterEnregistrer(@PathVariable("id") Integer id,
                                     @RequestParam("accepte") boolean accepte,
                                     @RequestParam(name = "motivation", required = false) String motivation,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Le gestionnaire est toujours la personne connectée (on ignore le champ du formulaire)
            reservationService.traiterReservation(id, moi().id(), accepte, motivation);
            redirectAttributes.addFlashAttribute("succes",
                    accepte ? "Réservation acceptée." : "Réservation refusée.");
            return "redirect:/reservations";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // on revient sur le formulaire de traitement avec le message d'erreur
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/reservations/" + id + "/traiter";
        }
    }

    // ---------- Suppression ----------

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if (estEmprunteur()) {
            Optional<Reservation> reservation = reservationService.getReservationById(id);
            if (reservation.isEmpty() || !estProprietaire(reservation.get())) {
                redirectAttributes.addFlashAttribute("erreur", "Cette réservation ne vous appartient pas.");
                return "redirect:/reservations";
            }
        }
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("succes", "Réservation supprimée.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/reservations";
    }

    // ---------- méthodes privées ----------

    private UtilisateurConnecte moi() {
        return UtilisateurConnecte.de(session);
    }

    private boolean estEmprunteur() {
        UtilisateurConnecte moi = moi();
        return moi != null && moi.estEmprunteur();
    }

    // Un admin / gestionnaire a accès à tout ; un emprunteur seulement à ses réservations
    private boolean estProprietaire(Reservation reservation) {
        return !estEmprunteur() || reservation.getEmprunteur().getIdUtilisateur() == moi().id();
    }

    private String afficherFormulaire(Model model, String titre, Reservation reservation) {
        Integer idEmprunteur = reservation.getEmprunteur() != null
                ? reservation.getEmprunteur().getIdUtilisateur() : null;
        Integer idType = reservation.getTypeMateriel() != null
                ? reservation.getTypeMateriel().getIdTypeMateriel() : null;

        List<Utilisateur> emprunteurs;
        if (estEmprunteur()) {
            // un emprunteur ne peut choisir que lui-même
            idEmprunteur = moi().id();
            emprunteurs = utilisateurService.getUtilisateurById(moi().id()).map(List::of).orElse(List.of());
        } else {
            emprunteurs = utilisateurService.getUtilisateursParRole(UtilisateurService.ROLE_EMPRUNTEUR);
        }

        model.addAttribute("titre", titre);
        model.addAttribute("reservation", reservation);
        model.addAttribute("emprunteursOptions", utilisateursOptions(emprunteurs, idEmprunteur));
        model.addAttribute("typesOptions", typesOptions(idType));
        return "reservations/form";
    }

    // Un gestionnaire ou un administrateur peut traiter une demande
    private List<Map<String, Object>> gestionnairesOptions(Integer idSelectionne) {
        List<Utilisateur> personnes = new ArrayList<>(
                utilisateurService.getUtilisateursParRole(UtilisateurService.ROLE_GESTIONNAIRE));
        personnes.addAll(utilisateurService.getUtilisateursParRole(UtilisateurService.ROLE_ADMIN));
        return utilisateursOptions(personnes, idSelectionne);
    }

    private List<Map<String, Object>> utilisateursOptions(List<Utilisateur> utilisateurs, Integer idSelectionne) {
        return utilisateurs.stream()
                .map(u -> option(u.getIdUtilisateur(), u.getPrenom() + " " + u.getNom(),
                        idSelectionne != null && idSelectionne == u.getIdUtilisateur()))
                .toList();
    }

    // Uniquement les types visibles au catalogue (le service refuse les autres)
    private List<Map<String, Object>> typesOptions(Integer idSelectionne) {
        return typeMaterielService.getTypesAuCatalogue().stream()
                .map(t -> option(t.getIdTypeMateriel(), t.getNom(),
                        idSelectionne != null && idSelectionne == t.getIdTypeMateriel()))
                .toList();
    }

    private static Map<String, Object> option(Object id, String label, boolean selected) {
        return Map.<String, Object>of("id", id, "label", label == null ? "" : label, "selected", selected);
    }
}