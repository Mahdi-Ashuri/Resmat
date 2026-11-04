package com.example.Resmat.repositories;

import com.example.Resmat.entities.TypeMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeMaterielRepositorie extends JpaRepository<TypeMateriel, Integer> {

    List<TypeMateriel> findByNomContainingIgnoreCase(String nom);

    List<TypeMateriel> findByCategorieIdCategorie(Integer idCategorie);

    List<TypeMateriel> findByAuCatalogueTrue();
}
