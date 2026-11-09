package com.example.Resmat.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Categorie;
import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.repositories.CategorieRepositorie;
import com.example.Resmat.repositories.MaterielRepositorie;
import com.example.Resmat.repositories.ReservationRepositorie;
import com.example.Resmat.repositories.TypeMaterielRepositorie;

@Service
public class TypeMaterielService {

    private final TypeMaterielRepositorie typeMaterielRepositorie;
    private final CategorieRepositorie categorieRepositorie;
    private final MaterielRepositorie materielRepositorie;
    private final ReservationRepositorie reservationRepositorie;

    public TypeMaterielService(TypeMaterielRepositorie typeMaterielRepositorie,
                               CategorieRepositorie categorieRepositorie,
                               MaterielRepositorie materielRepositorie,
                               ReservationRepositorie reservationRepositorie) {
        this.typeMaterielRepositorie = typeMaterielRepositorie;
        this.categorieRepositorie = categorieRepositorie;
        this.materielRepositorie = materielRepositorie;
        this.reservationRepositorie = reservationRepositorie;
    }

    public List<TypeMateriel> getAllTypesMateriel() {
        return typeMaterielRepositorie.findAll();
    }

    public Optional<TypeMateriel> getTypeMaterielById(Integer id) {
        return typeMaterielRepositorie.findById(id);
    }

    public List<TypeMateriel> rechercherTypeMateriel(String nom) {
        return typeMaterielRepositorie.findByNomContainingIgnoreCase(nom);
    }

    public List<TypeMateriel> getTypesParCategorie(Integer idCategorie) {
        return typeMaterielRepositorie.findByCategorieIdCategorie(idCategorie);
    }

    public List<TypeMateriel> getTypesAuCatalogue() {
        return typeMaterielRepositorie.findByAuCatalogueTrue();
    }

    @Transactional
    public TypeMateriel saveTypeMateriel(TypeMateriel typeMateriel) {
        if (typeMateriel.getNom() == null || typeMateriel.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du type de matériel est obligatoire.");
        }
        if (typeMateriel.getDureeMaxEmprunt() <= 0) {
            throw new IllegalArgumentException("La durée maximale d'emprunt doit être supérieure à 0.");
        }
        if (typeMateriel.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire.");
        }
        Categorie categorie = categorieRepositorie
                .findById(typeMateriel.getCategorie().getIdCategorie())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable."));

        typeMateriel.setNom(typeMateriel.getNom().trim());
        typeMateriel.setCategorie(categorie);
        return typeMaterielRepositorie.save(typeMateriel);
    }

    @Transactional
    public void deleteTypeMateriel(Integer id) {
        if (!typeMaterielRepositorie.existsById(id)) {
            throw new IllegalArgumentException("Type de matériel introuvable.");
        }
        if (materielRepositorie.existsByTypeMaterielIdTypeMateriel(id)) {
            throw new IllegalStateException(
                    "Suppression impossible : des matériels sont rattachés à ce type.");
        }
        if (!reservationRepositorie.findByTypeMaterielIdTypeMateriel(id).isEmpty()) {
            throw new IllegalStateException(
                    "Suppression impossible : des réservations concernent ce type.");
        }
        typeMaterielRepositorie.deleteById(id);
    }
}