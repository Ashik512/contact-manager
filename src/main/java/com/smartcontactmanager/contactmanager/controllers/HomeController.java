package com.smartcontactmanager.contactmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController  {

    @GetMapping("/home")
    public String home(Model model){
        model.addAttribute("title","Home-Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About-Contact Manager");
        return "about";
    }

}
