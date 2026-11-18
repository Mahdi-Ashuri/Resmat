
package com.example.Resmat.securite;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class LimiteurConnexion {

    private static final int MAX_PAR_LOGIN = 3;
    private static final int MAX_PAR_IP = 20;
    private static final Duration BLOCAGE = Duration.ofMinutes(1);

    private record Compteur(int echecs, Instant dernierEchec) {}

    private final Map<String, Compteur> compteurs = new HashMap<>();

    public synchronized long secondesRestantes(String login, String ip) {
        return Math.max(
                restant("login:" + login.toLowerCase(), MAX_PAR_LOGIN),
                restant("ip:" + ip, MAX_PAR_IP));
    }

    public synchronized void enregistrerEchec(String login, String ip) {
        nettoyer();
        incrementer("login:" + login.toLowerCase());
        incrementer("ip:" + ip);
    }

    public synchronized void reinitialiser(String login) {
        compteurs.remove("login:" + login.toLowerCase());
    }

    private long restant(String cle, int max) {
        Compteur c = compteurs.get(cle);

        if (c == null || c.echecs() < max) {
            return 0;
        }

        long s = Duration.between(
                Instant.now(),
                c.dernierEchec().plus(BLOCAGE)
        ).toSeconds();

        return Math.max(s, 0);
    }

    private void incrementer(String cle) {
        Compteur c = compteurs.get(cle);
        int n = (c == null) ? 1 : c.echecs() + 1;

        compteurs.put(cle, new Compteur(n, Instant.now()));
    }

    private void nettoyer() {
        Instant limite = Instant.now().minus(BLOCAGE);
        compteurs.values().removeIf(
                c -> c.dernierEchec().isBefore(limite)
        );
    }
}
