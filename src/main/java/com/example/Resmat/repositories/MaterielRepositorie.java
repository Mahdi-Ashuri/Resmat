package com.example.Resmat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Resmat.entities.Materiel;

public interface MaterielRepositorie extends JpaRepository<Materiel, Integer> {

    List<Materiel> findByNomContainingIgnoreCase(String nom);

    List<Materiel> findByDisponibleTrue();

    List<Materiel> findByDisponibleFalse();

    List<Materiel> findByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);

    List<Materiel> findByTypeMaterielIdTypeMaterielAndDisponibleTrue(Integer idTypeMateriel);
    
    @Query("""
            SELECT m FROM Materiel m
            WHERE LOWER(m.nom) LIKE LOWER(CONCAT('%', :motCle, '%'))
               OR LOWER(m.description) LIKE LOWER(CONCAT('%', :motCle, '%'))
               OR LOWER(m.typeMateriel.nom) LIKE LOWER(CONCAT('%', :motCle, '%'))
            ORDER BY m.nom
            """)
    List<Materiel> rechercherParMotCle(@Param("motCle") String motCle);

    boolean existsByTypeMaterielIdTypeMateriel(Integer idTypeMateriel);
}