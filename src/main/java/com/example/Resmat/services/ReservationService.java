package com.example.Resmat.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Reservation;
import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.repositories.EmpruntRepositorie;
import com.example.Resmat.repositories.MaterielRepositorie;
import com.example.Resmat.repositories.ReservationRepositorie;
import com.example.Resmat.repositories.TypeMaterielRepositorie;
import com.example.Resmat.repositories.UtilisateurRepositorie;

@Service
public class ReservationService {

    private final ReservationRepositorie reservationRepositorie;
    private final TypeMaterielRepositorie typeMaterielRepositorie;
    private final UtilisateurRepositorie utilisateurRepositorie;
    private final MaterielRepositorie materielRepositorie;
    private final EmpruntRepositorie empruntRepositorie;

    public ReservationService(ReservationRepositorie reservationRepositorie,
                              TypeMaterielRepositorie typeMaterielRepositorie,
                              UtilisateurRepositorie utilisateurRepositorie,
                              MaterielRepositorie materielRepositorie,
                              EmpruntRepositorie empruntRepositorie) {
        this.reservationRepositorie = reservationRepositorie;
        this.typeMaterielRepositorie = typeMaterielRepositorie;
        this.utilisateurRepositorie = utilisateurRepositorie;
        this.materielRepositorie = materielRepositorie;
        this.empruntRepositorie = empruntRepositorie;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepositorie.findAll();
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepositorie.findById(id);
    }

    public List<Reservation> getReservationsParEmprunteur(Integer idUtilisateur) {
        return reservationRepositorie.findByEmprunteurIdUtilisateur(idUtilisateur);
    }

    public List<Reservation> getReservationsParGestionnaire(Integer idGestionnaire) {
        return reservationRepositorie.findByGestionnaireIdUtilisateur(idGestionnaire);
    }

    public List<Reservation> getReservationsParTypeMateriel(Integer idTypeMateriel) {
        return reservationRepositorie.findByTypeMaterielIdTypeMateriel(idTypeMateriel);
    }

    public List<Reservation> getReservationsParPeriode(LocalDateTime debut, LocalDateTime fin) {
        return reservationRepositorie.findByDateDebutBetween(debut, fin);
    }

    public List<Reservation> getReservationsEnAttente() {
        return reservationRepositorie.findByAvisIsNull();
    }

    public List<Reservation> getReservationsAcceptees() {
        return reservationRepositorie.findByAvisTrue();
    }

    public List<Reservation> getReservationsRefusees() {
        return reservationRepositorie.findByAvisFalse();
    }

    public List<Reservation> getReservationsAccepteesSansEmprunt() {
        return reservationRepositorie.findAccepteesSansEmprunt();
    }

    // Création ou modification d'une demande (tant qu'elle n'a pas été traitée)
    @Transactional
    public Reservation saveReservation(Reservation reservation) {
        boolean nouvelle = reservation.getIdReservation() == 0;

        if (!nouvelle) {
            Reservation existante = reservationRepositorie.findById(reservation.getIdReservation())
                    .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));
            if (existante.getAvis() != null) {
                throw new IllegalStateException(
                        "Une réservation déjà traitée ne peut plus être modifiée.");
            }
            reservation.setDateResa(existante.getDateResa());
        }

        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (!reservation.getDateFin().isAfter(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }
        if (nouvelle && reservation.getDateDebut().toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
        }
        if (reservation.getTypeMateriel() == null) {
            throw new IllegalArgumentException("Le type de matériel est obligatoire.");
        }
        if (reservation.getEmprunteur() == null) {
            throw new IllegalArgumentException("L'emprunteur est obligatoire.");
        }

        TypeMateriel type = typeMaterielRepositorie
                .findById(reservation.getTypeMateriel().getIdTypeMateriel())
                .orElseThrow(() -> new IllegalArgumentException("Type de matériel introuvable."));
        Utilisateur emprunteur = utilisateurRepositorie
                .findById(reservation.getEmprunteur().getIdUtilisateur())
                .orElseThrow(() -> new IllegalArgumentException("Emprunteur introuvable."));

        if (!type.isAuCatalogue()) {
            throw new IllegalStateException("Ce type de matériel n'est pas disponible au catalogue.");
        }

        // dureeMaxEmprunt est exprimée en jours
        Duration dureeDemandee = Duration.between(reservation.getDateDebut(), reservation.getDateFin());
        if (dureeDemandee.compareTo(Duration.ofDays(type.getDureeMaxEmprunt())) > 0) {
            throw new IllegalArgumentException(
                    "La durée demandée dépasse la durée maximale d'emprunt (" + type.getDureeMaxEmprunt() + " jours).");
        }

        reservation.setTypeMateriel(type);
        reservation.setEmprunteur(emprunteur);

        if (nouvelle) {
            reservation.setDateResa(LocalDateTime.now());
            reservation.setAvis(null);
            reservation.setMotivationAvis(null);
            reservation.setGestionnaire(null);
        }

        return reservationRepositorie.save(reservation);
    }

    // Décision du gestionnaire : accepter ou refuser une demande en attente
    @Transactional
    public Reservation traiterReservation(Integer idReservation, Integer idGestionnaire,
                                          boolean accepte, String motivation) {
        Reservation reservation = reservationRepositorie.findById(idReservation)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));

        if (reservation.getAvis() != null) {
            throw new IllegalStateException("Cette réservation a déjà été traitée.");
        }

        Utilisateur gestionnaire = utilisateurRepositorie.findById(idGestionnaire)
                .orElseThrow(() -> new IllegalArgumentException("Gestionnaire introuvable."));
        String role = gestionnaire.getRole();
        if (!UtilisateurService.ROLE_GESTIONNAIRE.equals(role) && !UtilisateurService.ROLE_ADMIN.equals(role)) {
            throw new IllegalStateException("Seul un gestionnaire peut traiter une réservation.");
        }

        if (accepte) {
            Integer idType = reservation.getTypeMateriel().getIdTypeMateriel();
            long nbMateriels = materielRepositorie.findByTypeMaterielIdTypeMateriel(idType).size();
            long dejaReserves = reservationRepositorie.countAccepteesSurPeriode(
                    idType, reservation.getDateDebut(), reservation.getDateFin());
            if (dejaReserves >= nbMateriels) {
                throw new IllegalStateException(
                        "Plus de matériel de ce type disponible sur cette période.");
            }
        } else if (motivation == null || motivation.isBlank()) {
            throw new IllegalArgumentException("Une motivation est obligatoire pour un refus.");
        }

        reservation.setGestionnaire(gestionnaire);
        reservation.setAvis(accepte);
        reservation.setMotivationAvis(motivation);
        return reservationRepositorie.save(reservation);
    }

    @Transactional
    public void deleteReservation(Integer id) {
        if (!reservationRepositorie.existsById(id)) {
            throw new IllegalArgumentException("Réservation introuvable.");
        }
        if (empruntRepositorie.existsByReservationIdReservation(id)) {
            throw new IllegalStateException(
                    "Suppression impossible : un emprunt est lié à cette réservation.");
        }
        reservationRepositorie.deleteById(id);
    }
}