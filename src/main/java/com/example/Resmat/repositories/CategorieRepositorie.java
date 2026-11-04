package com.example.Resmat.repositories;

import com.example.Resmat.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorieRepositorie extends JpaRepository<Categorie, Integer> {

    List<Categorie> findByNomContainingIgnoreCase(String nom);
}
