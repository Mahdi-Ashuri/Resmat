package com.example.Resmat.services;

import com.example.Resmat.entities.Reservation;
import com.example.Resmat.repositories.ReservationRepositorie;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepositorie reservationRepositorie;

    public ReservationService(ReservationRepositorie reservationRepositorie) {
        this.reservationRepositorie = reservationRepositorie;
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

    public List<Reservation> getReservationsParPeriode(
            LocalDateTime debut, LocalDateTime fin) {
        return reservationRepositorie.findByDateDebutBetween(debut, fin);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepositorie.save(reservation);
    }

    public void deleteReservation(Integer id) {
        reservationRepositorie.deleteById(id);
    }
}
