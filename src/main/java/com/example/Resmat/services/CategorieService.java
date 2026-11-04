package com.example.Resmat.services;

import com.example.Resmat.entities.Categorie;
import com.example.Resmat.repositories.CategorieRepositorie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {

    private final CategorieRepositorie categorieRepositorie;

    public CategorieService(CategorieRepositorie categorieRepositorie) {
        this.categorieRepositorie = categorieRepositorie;
    }

    public List<Categorie> getAllCategories() {
        return categorieRepositorie.findAll();
    }

    public Optional<Categorie> getCategorieById(Integer id) {
        return categorieRepositorie.findById(id);
    }

    public List<Categorie> rechercherCategorie(String nom) {
        return categorieRepositorie.findByNomContainingIgnoreCase(nom);
    }

    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepositorie.save(categorie);
    }

    public void deleteCategorie(Integer id) {
        categorieRepositorie.deleteById(id);
    }
}
