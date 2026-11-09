package com.example.Resmat.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Emprunt;
import com.example.Resmat.entities.Materiel;
import com.example.Resmat.entities.Reservation;
import com.example.Resmat.repositories.EmpruntRepositorie;
import com.example.Resmat.repositories.MaterielRepositorie;
import com.example.Resmat.repositories.ReservationRepositorie;

@Service
public class EmpruntService {

    private final EmpruntRepositorie empruntRepositorie;
    private final ReservationRepositorie reservationRepositorie;
    private final MaterielRepositorie materielRepositorie;

    public EmpruntService(EmpruntRepositorie empruntRepositorie,
                          ReservationRepositorie reservationRepositorie,
                          MaterielRepositorie materielRepositorie) {
        this.empruntRepositorie = empruntRepositorie;
        this.reservationRepositorie = reservationRepositorie;
        this.materielRepositorie = materielRepositorie;
    }

    public List<Emprunt> getAllEmprunts() {
        return empruntRepositorie.findAll();
    }

    public Optional<Emprunt> getEmpruntById(Integer id) {
        return empruntRepositorie.findById(id);
    }

    public List<Emprunt> getEmpruntsParMateriel(Integer idMateriel) {
        return empruntRepositorie.findByMaterielIdMateriel(idMateriel);
    }

    public Optional<Emprunt> getEmpruntParReservation(Integer idReservation) {
        return empruntRepositorie.findByReservationIdReservation(idReservation);
    }

    public List<Emprunt> getEmpruntsNonRetournes() {
        return empruntRepositorie.findByDateRetourEffectiveIsNull();
    }

    public List<Emprunt> getEmpruntsAvecDegradation() {
        return empruntRepositorie.findByDegradationTrue();
    }

    @Transactional
    public Emprunt enregistrerRemise(Integer idReservation, Integer idMateriel, String commentaires) {
        Reservation reservation = reservationRepositorie.findById(idReservation)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable."));

        if (!Boolean.TRUE.equals(reservation.getAvis())) {
            throw new IllegalStateException("Cette réservation n'a pas été acceptée.");
        }
        if (empruntRepositorie.existsByReservationIdReservation(idReservation)) {
            throw new IllegalStateException("Un emprunt existe déjà pour cette réservation.");
        }

        Materiel materiel = materielRepositorie.findById(idMateriel)
                .orElseThrow(() -> new IllegalArgumentException("Matériel introuvable."));

        if (materiel.getTypeMateriel().getIdTypeMateriel()
                != reservation.getTypeMateriel().getIdTypeMateriel()) {
            throw new IllegalStateException(
                    "Ce matériel n'est pas du type réservé.");
        }
        if (!materiel.isDisponible()
                || empruntRepositorie.existsByMaterielIdMaterielAndDateRetourEffectiveIsNull(idMateriel)) {
            throw new IllegalStateException("Ce matériel n'est pas disponible.");
        }

        Emprunt emprunt = new Emprunt();
        emprunt.setReservation(reservation);
        emprunt.setMateriel(materiel);
        emprunt.setDateRemise(LocalDateTime.now());
        emprunt.setDateRetourPrevue(reservation.getDateFin());
        emprunt.setCommentaires(commentaires);
        emprunt.setDegradation(false);

        materiel.setDisponible(false);
        materielRepositorie.save(materiel);

        return empruntRepositorie.save(emprunt);
    }

    @Transactional
    public Emprunt enregistrerRetour(Integer idEmprunt, boolean degradation, String commentaires) {
        Emprunt emprunt = empruntRepositorie.findById(idEmprunt)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt introuvable."));

        if (emprunt.getDateRetourEffective() != null) {
            throw new IllegalStateException("Ce matériel a déjà été rendu.");
        }

        emprunt.setDateRetourEffective(LocalDateTime.now());
        emprunt.setDegradation(degradation);
        if (commentaires != null && !commentaires.isBlank()) {
            emprunt.setCommentaires(commentaires);
        }

        Materiel materiel = emprunt.getMateriel();
        materiel.setDisponible(!degradation);
        materielRepositorie.save(materiel);

        return empruntRepositorie.save(emprunt);
    }

    @Transactional
    public Emprunt saveEmprunt(Emprunt emprunt) {
        if (emprunt.getMateriel() == null || emprunt.getReservation() == null) {
            throw new IllegalArgumentException("Le matériel et la réservation sont obligatoires.");
        }
        if (emprunt.getDateRemise() == null || emprunt.getDateRetourPrevue() == null) {
            throw new IllegalArgumentException("Les dates de remise et de retour prévue sont obligatoires.");
        }
        if (!emprunt.getDateRetourPrevue().isAfter(emprunt.getDateRemise())) {
            throw new IllegalArgumentException("Le retour prévu doit être après la remise.");
        }
        if (emprunt.getIdEmprunt() == 0
                && empruntRepositorie.existsByReservationIdReservation(
                        emprunt.getReservation().getIdReservation())) {
            throw new IllegalStateException("Un emprunt existe déjà pour cette réservation.");
        }
        return empruntRepositorie.save(emprunt);
    }

    @Transactional
    public void deleteEmprunt(Integer id) {
        Emprunt emprunt = empruntRepositorie.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt introuvable."));

        if (emprunt.getDateRetourEffective() == null) {
            Materiel materiel = emprunt.getMateriel();
            materiel.setDisponible(true);
            materielRepositorie.save(materiel);
        }
        empruntRepositorie.deleteById(id);
    }
}