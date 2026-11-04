package com.example.Resmat.repositories;

import com.example.Resmat.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepositorie extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByEmprunteurIdUtilisateur(Integer idUtilisateur);

    List<Reservation> findByGestionnaireIdUtilisateur(Integer idGestionnaire);

    List<Reservation> findByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);

    List<Reservation> findByDateDebutBetween(
            java.time.LocalDateTime debut,
            java.time.LocalDateTime fin
    );
}
