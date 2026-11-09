package com.example.Resmat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Resmat.entities.Categorie;

public interface CategorieRepositorie extends JpaRepository<Categorie, Integer> {

    List<Categorie> findByNomContainingIgnoreCase(String nom);
}