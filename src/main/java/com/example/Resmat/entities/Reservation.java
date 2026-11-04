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

    @Column(name = "dateDebut")
    private LocalDateTime dateDebut;

    @Column(name = "dateFin")
    private LocalDateTime dateFin;

    @Column(name = "commentaire", length = 64000)
    private String commentaire;

    @Column(name = "dateResa")
    private LocalDateTime dateResa;

    @Column(name = "avis")
    private boolean avis;

    @Column(name = "motivationAvis", length = 64000)
    private String motivationAvis;

    @ManyToOne
    @JoinColumn(name = "gestionnaireId")
    private Utilisateur gestionnaire;

    @ManyToOne
    @JoinColumn(name = "Id_TypeMateriel")
    private TypeMateriel typeMateriel;

    @ManyToOne
    @JoinColumn(name = "emprunteurId")
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

    public boolean isAvis() {
        return avis;
    }

    public void setAvis(boolean avis) {
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
