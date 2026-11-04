package com.example.Resmat.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TypeMateriel")
public class TypeMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_TypeMateriel")
    private int idTypeMateriel;

    @Column(name = "nom", length = 150)
    private String nom;

    @Column(name = "description", length = 64000)
    private String description;

    @Column(name = "dureeMaxEmprunt")
    private int dureeMaxEmprunt;

    @Column(name = "auCatalogue")
    private boolean auCatalogue;

    @ManyToOne
    @JoinColumn(name = "Id_Categorie")
    private Categorie categorie;

    public TypeMateriel() {
    }

    public int getIdTypeMateriel() {
        return idTypeMateriel;
    }

    public void setIdTypeMateriel(int idTypeMateriel) {
        this.idTypeMateriel = idTypeMateriel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDureeMaxEmprunt() {
        return dureeMaxEmprunt;
    }

    public void setDureeMaxEmprunt(int dureeMaxEmprunt) {
        this.dureeMaxEmprunt = dureeMaxEmprunt;
    }

    public boolean isAuCatalogue() {
        return auCatalogue;
    }

    public void setAuCatalogue(boolean auCatalogue) {
        this.auCatalogue = auCatalogue;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
}
