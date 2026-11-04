package com.example.Resmat.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Emprunt")
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Emprunt")
    private int idEmprunt;

    @Column(name = "dateRemise")
    private LocalDateTime dateRemise;

    @Column(name = "dateRetourPrevue")
    private LocalDateTime dateRetourPrevue;

    @Column(name = "dateretourEffective")
    private LocalDateTime dateRetourEffective;

    @Column(name = "commentaires", length = 64000)
    private String commentaires;

    @Column(name = "degradation")
    private boolean degradation;

    @ManyToOne
    @JoinColumn(name = "Id_Materiel")
    private Materiel materiel;

    @OneToOne
    @JoinColumn(name = "Id_Reservation")
    private Reservation reservation;

    public Emprunt() {
    }

    public int getIdEmprunt() {
        return idEmprunt;
    }

    public void setIdEmprunt(int idEmprunt) {
        this.idEmprunt = idEmprunt;
    }

    public LocalDateTime getDateRemise() {
        return dateRemise;
    }

    public void setDateRemise(LocalDateTime dateRemise) {
        this.dateRemise = dateRemise;
    }

    public LocalDateTime getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDateTime dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDateTime getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDateTime dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public boolean isDegradation() {
        return degradation;
    }

    public void setDegradation(boolean degradation) {
        this.degradation = degradation;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
