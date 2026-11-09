package com.example.Resmat.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Resmat.entities.Materiel;
import com.example.Resmat.entities.TypeMateriel;
import com.example.Resmat.repositories.EmpruntRepositorie;
import com.example.Resmat.repositories.MaterielRepositorie;
import com.example.Resmat.repositories.TypeMaterielRepositorie;

@Service
public class MaterielService {

    private final MaterielRepositorie materielRepositorie;
    private final TypeMaterielRepositorie typeMaterielRepositorie;
    private final EmpruntRepositorie empruntRepositorie;

    public MaterielService(MaterielRepositorie materielRepositorie,
                           TypeMaterielRepositorie typeMaterielRepositorie,
                           EmpruntRepositorie empruntRepositorie) {
        this.materielRepositorie = materielRepositorie;
        this.typeMaterielRepositorie = typeMaterielRepositorie;
        this.empruntRepositorie = empruntRepositorie;
    }

    public List<Materiel> getAllMateriels() {
        return materielRepositorie.findAll();
    }

    public Optional<Materiel> getMaterielById(Integer id) {
        return materielRepositorie.findById(id);
    }

    public List<Materiel> rechercherMateriel(String nom) {
        return materielRepositorie.findByNomContainingIgnoreCase(nom);
    }

    public List<Materiel> getMaterielsDisponibles() {
        return materielRepositorie.findByDisponibleTrue();
    }

    public List<Materiel> getMaterielsIndisponibles() {
        return materielRepositorie.findByDisponibleFalse();
    }

    public List<Materiel> getMaterielsParType(Integer idTypeMateriel) {
        return materielRepositorie.findByTypeMaterielIdTypeMateriel(idTypeMateriel);
    }

    // Exemplaires libres d'un type (pour choisir quoi remettre à l'emprunteur)
    public List<Materiel> getMaterielsDisponiblesParType(Integer idTypeMateriel) {
        return materielRepositorie.findByTypeMaterielIdTypeMaterielAndDisponibleTrue(idTypeMateriel);
    }

    @Transactional
    public Materiel saveMateriel(Materiel materiel) {
        if (materiel.getNom() == null || materiel.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du matériel est obligatoire.");
        }
        if (materiel.getTypeMateriel() == null) {
            throw new IllegalArgumentException("Le type de matériel est obligatoire.");
        }
        TypeMateriel type = typeMaterielRepositorie
                .findById(materiel.getTypeMateriel().getIdTypeMateriel())
                .orElseThrow(() -> new IllegalArgumentException("Type de matériel introuvable."));

        materiel.setNom(materiel.getNom().trim());
        materiel.setTypeMateriel(type);
        return materielRepositorie.save(materiel);
    }

    @Transactional
    public void deleteMateriel(Integer id) {
        if (!materielRepositorie.existsById(id)) {
            throw new IllegalArgumentException("Matériel introuvable.");
        }
        if (!empruntRepositorie.findByMaterielIdMateriel(id).isEmpty()) {
            throw new IllegalStateException(
                    "Suppression impossible : ce matériel a un historique d'emprunts. "
                  + "Rends-le indisponible à la place.");
        }
        materielRepositorie.deleteById(id);
    }
}