package com.example.Resmat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Resmat.entities.Emprunt;

public interface EmpruntRepositorie extends JpaRepository<Emprunt, Integer> {

    List<Emprunt> findByMaterielIdMateriel(Integer idMateriel);

    Optional<Emprunt> findByReservationIdReservation(Integer idReservation);

    boolean existsByReservationIdReservation(Integer idReservation);

    List<Emprunt> findByDateRetourEffectiveIsNull();

    boolean existsByMaterielIdMaterielAndDateRetourEffectiveIsNull(Integer idMateriel);

    List<Emprunt> findByDegradationTrue();
}