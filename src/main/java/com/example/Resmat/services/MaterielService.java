package com.example.Resmat.services;

import com.example.Resmat.entities.Materiel;
import com.example.Resmat.repositories.MaterielRepositorie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterielService {

    private final MaterielRepositorie materielRepositorie;

    public MaterielService(MaterielRepositorie materielRepositorie) {
        this.materielRepositorie = materielRepositorie;
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

    public Materiel saveMateriel(Materiel materiel) {
        return materielRepositorie.save(materiel);
    }

    public void deleteMateriel(Integer id) {
        materielRepositorie.deleteById(id);
    }
}
