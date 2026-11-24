package com.example.Resmat.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final PhotoService photoService;

    public MaterielService(MaterielRepositorie materielRepositorie,
                           TypeMaterielRepositorie typeMaterielRepositorie,
                           EmpruntRepositorie empruntRepositorie,
                           PhotoService photoService) {

        this.materielRepositorie = materielRepositorie;
        this.typeMaterielRepositorie = typeMaterielRepositorie;
        this.empruntRepositorie = empruntRepositorie;
        this.photoService = photoService;
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

    // US-2.4 : sans mot clé, on renvoie tout le catalogue
    public List<Materiel> rechercherParMotCle(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            return materielRepositorie.findAll();
        }
        return materielRepositorie.rechercherParMotCle(motCle.trim());
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

    public List<Materiel> getMaterielsDisponiblesParType(Integer idTypeMateriel) {
        return materielRepositorie
                .findByTypeMaterielIdTypeMaterielAndDisponibleTrue(idTypeMateriel);
    }

    @Transactional
    public Materiel saveMateriel(Materiel materiel) {

        if (materiel.getNom() == null || materiel.getNom().isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du matériel est obligatoire.");
        }

        if (materiel.getTypeMateriel() == null) {
            throw new IllegalArgumentException(
                    "Le type de matériel est obligatoire.");
        }

        TypeMateriel type = typeMaterielRepositorie
                .findById(materiel.getTypeMateriel().getIdTypeMateriel())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Type de matériel introuvable."));

        materiel.setNom(materiel.getNom().trim());
        materiel.setTypeMateriel(type);

        return materielRepositorie.save(materiel);
    }

    @Transactional
    public Materiel saveMateriel(Materiel materiel,
                                 MultipartFile photo,
                                 boolean supprimerPhoto) {

        String ancienne = null;

        if (materiel.getIdMateriel() != 0) {

            ancienne = materielRepositorie
                    .findById(materiel.getIdMateriel())
                    .map(Materiel::getPhoto)
                    .orElse(null);
        }

        materiel.setPhoto(ancienne);

        String nouvelle = null;

        if (photo != null && !photo.isEmpty()) {

            nouvelle = photoService.enregistrer(photo);
            materiel.setPhoto(nouvelle);

        } else if (supprimerPhoto) {

            materiel.setPhoto(null);
        }

        try {

            Materiel sauve = saveMateriel(materiel);

            if (!Objects.equals(ancienne, sauve.getPhoto())) {
                photoService.supprimer(ancienne);
            }

            return sauve;

        } catch (RuntimeException e) {

            photoService.supprimer(nouvelle);
            materiel.setPhoto(ancienne);

            throw e;
        }
    }

    @Transactional
    public void deleteMateriel(Integer id) {

        Materiel materiel = materielRepositorie
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Matériel introuvable."));

        if (!empruntRepositorie
                .findByMaterielIdMateriel(id)
                .isEmpty()) {

            throw new IllegalStateException(
                    "Suppression impossible : ce matériel a un historique d'emprunts. "
                            + "Rends-le indisponible à la place.");
        }

        materielRepositorie.deleteById(id);

        photoService.supprimer(materiel.getPhoto());
    }
}