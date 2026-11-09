package com.example.Resmat.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Resmat.entities.Reservation;

public interface ReservationRepositorie extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByEmprunteurIdUtilisateur(Integer idUtilisateur);

    List<Reservation> findByGestionnaireIdUtilisateur(Integer idGestionnaire);

    List<Reservation> findByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);

    List<Reservation> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);

    List<Reservation> findByAvisIsNull();

    List<Reservation> findByAvisTrue();

    List<Reservation> findByAvisFalse();

    @Query("""
           SELECT r FROM Reservation r
           WHERE r.avis = true
             AND NOT EXISTS (SELECT e FROM Emprunt e WHERE e.reservation = r)
           """)
    List<Reservation> findAccepteesSansEmprunt();

    @Query("""
           SELECT COUNT(r) FROM Reservation r
           WHERE r.typeMateriel.idTypeMateriel = :idType
             AND r.avis = true
             AND r.dateDebut < :fin
             AND r.dateFin > :debut
           """)
    long countAccepteesSurPeriode(@Param("idType") Integer idType,
                                  @Param("debut") LocalDateTime debut,
                                  @Param("fin") LocalDateTime fin);
}