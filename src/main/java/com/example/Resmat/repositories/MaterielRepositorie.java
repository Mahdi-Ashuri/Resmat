package com.example.Resmat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Resmat.entities.Materiel;

public interface MaterielRepositorie extends JpaRepository<Materiel, Integer> {

    List<Materiel> findByNomContainingIgnoreCase(String nom);

    List<Materiel> findByDisponibleTrue();

    List<Materiel> findByDisponibleFalse();

    List<Materiel> findByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);

    List<Materiel> findByTypeMaterielIdTypeMaterielAndDisponibleTrue(Integer idTypeMateriel);

    boolean existsByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);
}