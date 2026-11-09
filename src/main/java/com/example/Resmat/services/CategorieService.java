package com.example.Resmat.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Categorie;
import com.example.Resmat.repositories.CategorieRepositorie;
import com.example.Resmat.repositories.TypeMaterielRepositorie;

@Service
public class CategorieService {

    private final CategorieRepositorie categorieRepositorie;
    private final TypeMaterielRepositorie typeMaterielRepositorie;

    public CategorieService(CategorieRepositorie categorieRepositorie,
                            TypeMaterielRepositorie typeMaterielRepositorie) {
        this.categorieRepositorie = categorieRepositorie;
        this.typeMaterielRepositorie = typeMaterielRepositorie;
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

    @Transactional
    public Categorie saveCategorie(Categorie categorie) {
        if (categorie.getNom() == null || categorie.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la catégorie est obligatoire.");
        }
        categorie.setNom(categorie.getNom().trim());
        return categorieRepositorie.save(categorie);
    }

    @Transactional
    public void deleteCategorie(Integer id) {
        if (!categorieRepositorie.existsById(id)) {
            throw new IllegalArgumentException("Catégorie introuvable.");
        }
        if (typeMaterielRepositorie.existsByCategorieIdCategorie(id)) {
            throw new IllegalStateException(
                    "Suppression impossible : des types de matériel appartiennent à cette catégorie.");
        }
        categorieRepositorie.deleteById(id);
    }
}