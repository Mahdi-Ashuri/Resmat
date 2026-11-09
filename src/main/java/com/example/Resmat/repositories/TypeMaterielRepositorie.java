package com.example.Resmat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Resmat.entities.TypeMateriel;

public interface TypeMaterielRepositorie extends JpaRepository<TypeMateriel, Integer> {

    List<TypeMateriel> findByNomContainingIgnoreCase(String nom);

    List<TypeMateriel> findByCategorieIdCategorie(Integer idCategorie);

    List<TypeMateriel> findByAuCatalogueTrue();

    // Pour empêcher la suppression d'une catégorie encore utilisée
    boolean existsByCategorieIdCategorie(Integer idCategorie);
}
