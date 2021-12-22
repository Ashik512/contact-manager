package com.smartcontactmanager.contactmanager.controllers;

import com.smartcontactmanager.contactmanager.entities.User;
import com.smartcontactmanager.contactmanager.helpers.Message;
import com.smartcontactmanager.contactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController  {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home-Contact Manager");
        return "user/home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About-Contact Manager");
        return "user/about";
    }

    @GetMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title","Register-Contact Manager");
        model.addAttribute("user",new User());
        return "user/signup";
    }

    //handler for register user
    @PostMapping("/do-register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult error, @RequestParam(value = "aggrement", defaultValue = "false") boolean aggrement, Model model, HttpSession session) {
        model.addAttribute("title","Register-Contact Manager");
        try {
            if (!aggrement) {
                System.out.println("you haven't accepted our terms and conditions!");
                throw new Exception("you haven't accepted our terms and conditions!");
            }
            if(error.hasErrors()){
                System.out.println("Error "+error.toString());
                model.addAttribute("user", user);
                return "user/signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User result = userRepository.save(user);
            model.addAttribute("user", new User());

            session.setAttribute("message" ,new Message("Successfully Registered !","alert-success"));
            return "user/signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
            return "user/signup";
        }

    }

    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("tile", "Login - Contact Manager");
        return "user/login";
    }
}
