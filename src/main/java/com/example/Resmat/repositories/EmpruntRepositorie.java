package com.example.Resmat.repositories;

import com.example.Resmat.entities.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpruntRepositorie extends JpaRepository<Emprunt, Integer> {

    List<Emprunt> findByMaterielIdMateriel(Integer idMateriel);

    List<Emprunt> findByReservationIdReservation(Integer idReservation);

    List<Emprunt> findByDateRetourEffectiveIsNull();

    List<Emprunt> findByDegradationTrue();
}
