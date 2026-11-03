package com.example.Resmat;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ResmatController {

    @GetMapping("/")
    public String accueil(Model model) {
        model.addAttribute("message", "Bienvenue dans mon projet Resmat !");
        return "accueil";
    }
}
