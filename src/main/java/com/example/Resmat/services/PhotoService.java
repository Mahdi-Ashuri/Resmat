package com.example.Resmat.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    private static final long TAILLE_MAX = 5L * 1024 * 1024;

    private final Path dossier;

    public PhotoService(@Value("${app.upload.dir}") String dir) throws IOException {
        this.dossier = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(dossier);
    }

    public Path getDossier() {
        return dossier;
    }

    public String enregistrer(MultipartFile fichier) {

        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier sélectionné.");
        }

        if (fichier.getSize() > TAILLE_MAX) {
            throw new IllegalArgumentException("La photo dépasse 5 Mo.");
        }

        String type = fichier.getContentType();

        String ext;

        if ("image/jpeg".equals(type)) {
            ext = ".jpg";
        } else if ("image/png".equals(type)) {
            ext = ".png";
        } else {
            throw new IllegalArgumentException(
                    "Format non accepté (JPEG ou PNG uniquement).");
        }

        try {

            byte[] data = fichier.getBytes();

            if (ImageIO.read(new ByteArrayInputStream(data)) == null) {
                throw new IllegalArgumentException(
                        "Le fichier n'est pas une image valide.");
            }

            String nom = UUID.randomUUID() + ext;

            Files.write(dossier.resolve(nom), data);

            return nom;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Impossible d'enregistrer la photo.", e);
        }
    }

    public void supprimer(String nom) {

        if (nom == null || nom.isBlank()) {
            return;
        }

        try {

            Path p = dossier.resolve(nom).normalize();

            if (p.startsWith(dossier)) {
                Files.deleteIfExists(p);
            }

        } catch (IOException ignored) {
        }
    }
}