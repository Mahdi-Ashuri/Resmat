package com.example.Resmat.services;

import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.repositories.TypeMaterielRepositorie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeMaterielService {

    private final TypeMaterielRepositorie typeMaterielRepositorie;

    public TypeMaterielService(TypeMaterielRepositorie typeMaterielRepositorie) {
        this.typeMaterielRepositorie = typeMaterielRepositorie;
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

    public TypeMateriel saveTypeMateriel(TypeMateriel typeMateriel) {
        return typeMaterielRepositorie.save(typeMateriel);
    }

    public void deleteTypeMateriel(Integer id) {
        typeMaterielRepositorie.deleteById(id);
    }
}
