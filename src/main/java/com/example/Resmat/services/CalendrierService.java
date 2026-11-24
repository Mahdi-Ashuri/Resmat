package com.example.Resmat.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.Resmat.entities.Reservation;
import com.example.Resmat.repositories.MaterielRepositorie;
import com.example.Resmat.repositories.ReservationRepositorie;

@Service
public class CalendrierService {

    private final ReservationRepositorie reservationRepositorie;
    private final MaterielRepositorie materielRepositorie;

    public CalendrierService(ReservationRepositorie reservationRepositorie,
                             MaterielRepositorie materielRepositorie) {
        this.reservationRepositorie = reservationRepositorie;
        this.materielRepositorie = materielRepositorie;
    }

    // Nombre d'exemplaires du type (même règle que l'acceptation d'une réservation)
    public int capacite(Integer idType) {
        return (int) materielRepositorie.countByTypeMaterielIdTypeMateriel(idType);
    }

    // Grille du mois : une liste de semaines, chaque semaine contient 7 jours (lundi → dimanche)
    public List<Map<String, Object>> construireSemaines(Integer idType, YearMonth mois) {
        int capacite = capacite(idType);

        LocalDate debutGrille = mois.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finGrille = mois.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Reservation> acceptees = reservationRepositorie.findAccepteesSurPeriode(
                idType, debutGrille.atStartOfDay(), finGrille.plusDays(1).atStartOfDay());

        LocalDate aujourdhui = LocalDate.now();
        List<Map<String, Object>> semaines = new ArrayList<>();
        List<Map<String, Object>> jours = new ArrayList<>();

        for (LocalDate jour = debutGrille; !jour.isAfter(finGrille); jour = jour.plusDays(1)) {
            LocalDateTime debutJour = jour.atStartOfDay();
            LocalDateTime finJour = jour.plusDays(1).atStartOfDay();

            // réservations acceptées qui touchent ce jour
            long occupes = acceptees.stream()
                    .filter(r -> r.getDateDebut().isBefore(finJour) && r.getDateFin().isAfter(debutJour))
                    .count();
            int libres = (int) Math.max(0, capacite - occupes);

            boolean passe = jour.isBefore(aujourdhui);
            boolean actif = !passe && capacite > 0;

            // Toutes les clés sont toujours présentes (Mustache ne remonte pas chercher une clé absente)
            Map<String, Object> cellule = new HashMap<>();
            cellule.put("numero", jour.getDayOfMonth());
            cellule.put("date", jour.toString());                       // ex. 2026-11-20
            cellule.put("horsMois", !YearMonth.from(jour).equals(mois));
            cellule.put("aujourdhui", jour.equals(aujourdhui));
            cellule.put("passe", passe);
            cellule.put("libres", libres);
            cellule.put("capacite", capacite);
            cellule.put("libre", actif && libres == capacite);
            cellule.put("partiel", actif && libres > 0 && libres < capacite);
            cellule.put("complet", actif && libres == 0);
            cellule.put("reservable", actif && libres > 0);
            jours.add(cellule);

            if (jours.size() == 7) {
                semaines.add(Map.of("jours", jours));
                jours = new ArrayList<>();
            }
        }
        return semaines;
    }
}