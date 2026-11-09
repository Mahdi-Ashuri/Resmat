package com.example.Resmat.securite;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.example.Resmat.entities.Utilisateur;
import com.example.Resmat.services.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UtilisateurService utilisateurService;

    public AuthInterceptor(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        UtilisateurConnecte moi = UtilisateurConnecte.de(session);

        if (moi == null) {
            response.sendRedirect("/connexion");
            return false;
        }

        Optional<Utilisateur> enBase = utilisateurService.getUtilisateurById(moi.id());
        if (enBase.isEmpty()) {
            session.invalidate();
            response.sendRedirect("/connexion");
            return false;
        }
        Utilisateur u = enBase.get();
        moi = new UtilisateurConnecte(u.getIdUtilisateur(), u.getPrenom() + " " + u.getNom(), u.getRole());
        session.setAttribute(UtilisateurConnecte.SESSION_KEY, moi);

        if (!autorise(moi, request.getRequestURI())) {
            FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
            flash.put("erreur", "Accès refusé : vous n'avez pas les droits pour cette page.");
            RequestContextUtils.saveOutputFlashMap("/", request, response);
            response.sendRedirect("/");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        if (modelAndView == null || modelAndView.getViewName() == null
                || modelAndView.getViewName().startsWith("redirect:")) {
            return;
        }
        UtilisateurConnecte moi = UtilisateurConnecte.de(request.getSession(false));
        if (moi == null) {
            return;
        }
        modelAndView.addObject("connecte", true);
        modelAndView.addObject("nomConnecte", moi.nom());
        modelAndView.addObject("roleConnecte", moi.role());
        modelAndView.addObject("estAdmin", moi.estAdmin());
        modelAndView.addObject("peutGerer", moi.peutGerer());
    }

    private boolean autorise(UtilisateurConnecte moi, String chemin) {
        if (moi.estAdmin()) {
            return true;
        }
        if (chemin.equals("/") || chemin.equals("/deconnexion")) {
            return true;
        }
        if (moi.estGestionnaire()) {
            return !chemin.startsWith("/utilisateurs");
        }
        if (moi.estEmprunteur()) {
            return chemin.startsWith("/reservations") && !chemin.endsWith("/traiter");
        }
        return false;
    }
}