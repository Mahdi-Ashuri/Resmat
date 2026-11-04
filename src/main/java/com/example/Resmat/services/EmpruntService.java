package com.example.Resmat.services;

import com.example.Resmat.entities.Emprunt;
import com.example.Resmat.repositories.EmpruntRepositorie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {

    private final EmpruntRepositorie empruntRepositorie;

    public EmpruntService(EmpruntRepositorie empruntRepositorie) {
        this.empruntRepositorie = empruntRepositorie;
    }

    public List<Emprunt> getAllEmprunts() {
        return empruntRepositorie.findAll();
    }

    public Optional<Emprunt> getEmpruntById(Integer id) {
        return empruntRepositorie.findById(id);
    }

    public List<Emprunt> getEmpruntsParMateriel(Integer idMateriel) {
        return empruntRepositorie.findByMaterielIdMateriel(idMateriel);
    }

    public List<Emprunt> getEmpruntsParReservation(Integer idReservation) {
        return empruntRepositorie.findByReservationIdReservation(idReservation);
    }

    public List<Emprunt> getEmpruntsNonRetournes() {
        return empruntRepositorie.findByDateRetourEffectiveIsNull();
    }

    public List<Emprunt> getEmpruntsAvecDegradation() {
        return empruntRepositorie.findByDegradationTrue();
    }

    public Emprunt saveEmprunt(Emprunt emprunt) {
        return empruntRepositorie.save(emprunt);
    }

    public void deleteEmprunt(Integer id) {
        empruntRepositorie.deleteById(id);
    }
}
