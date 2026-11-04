package com.example.Resmat.repositories;

import com.example.Resmat.entities.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterielRepositorie extends JpaRepository<Materiel, Integer> {

    List<Materiel> findByNomContainingIgnoreCase(String nom);

    List<Materiel> findByDisponibleTrue();

    List<Materiel> findByDisponibleFalse();

    List<Materiel> findByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);
}
