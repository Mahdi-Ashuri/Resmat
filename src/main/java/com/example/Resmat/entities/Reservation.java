package com.example.Resmat.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Reservation")
    private int idReservation;

    @Column(name = "dateDebut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "dateFin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "commentaire", length = 64000)
    private String commentaire;

    @Column(name = "dateResa", nullable = false)
    private LocalDateTime dateResa = LocalDateTime.now();

    @Column(name = "avis")
    private Boolean avis;

    @Column(name = "motivationAvis", length = 64000)
    private String motivationAvis;

    @ManyToOne
    @JoinColumn(name = "gestionnaireId")
    private Utilisateur gestionnaire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_TypeMateriel", nullable = false)
    private TypeMateriel typeMateriel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emprunteurId", nullable = false)
    private Utilisateur emprunteur;

    public Reservation() {
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateResa() {
        return dateResa;
    }

    public void setDateResa(LocalDateTime dateResa) {
        this.dateResa = dateResa;
    }

    public Boolean getAvis() {
        return avis;
    }

    public void setAvis(Boolean avis) {
        this.avis = avis;
    }

    public String getMotivationAvis() {
        return motivationAvis;
    }

    public void setMotivationAvis(String motivationAvis) {
        this.motivationAvis = motivationAvis;
    }

    public Utilisateur getGestionnaire() {
        return gestionnaire;
    }

    public void setGestionnaire(Utilisateur gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public TypeMateriel getTypeMateriel() {
        return typeMateriel;
    }

    public void setTypeMateriel(TypeMateriel typeMateriel) {
        this.typeMateriel = typeMateriel;
    }

    public Utilisateur getEmprunteur() {
        return emprunteur;
    }

    public void setEmprunteur(Utilisateur emprunteur) {
        this.emprunteur = emprunteur;
    }
}