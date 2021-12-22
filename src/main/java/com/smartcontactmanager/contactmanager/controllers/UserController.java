package com.smartcontactmanager.contactmanager.controllers;

import com.smartcontactmanager.contactmanager.entities.Contact;
import com.smartcontactmanager.contactmanager.entities.User;
import com.smartcontactmanager.contactmanager.helpers.Message;
import com.smartcontactmanager.contactmanager.repositories.ContactRepository;
import com.smartcontactmanager.contactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    //adding common data handler
    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String name = principal.getName();
        System.out.println(name);

        User user = userRepository.getUserByUserName(name);
        System.out.println(user);

        model.addAttribute("user",user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model){

        return "user/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String addContactForm(Model model){

        model.addAttribute("title","Add Contact - Contact Manager");
        model.addAttribute("contact", new Contact());

        return "user/add-contact-form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("contactPhoto") MultipartFile file, Principal principal, HttpSession session){
        //System.out.println(contact);
        try{
            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);
            //System.out.println("Id "+user.getId());
            contact.setUser(user);

            // file uploading
            if(file.isEmpty()){
                System.out.println("File is empty");
                contact.setPhoto("default.png");
                //throw new Exception("File is empty!");
            }else{
                contact.setPhoto(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image Uploaded...");
            }

            Contact result = contactRepository.save(contact);
            if(result == null){
                throw new Exception("Contact Saving Failed!");
            }else{
                System.out.println("contact added successfully!");
                // success message
                session.setAttribute("message" ,new Message("Contact Added Successfully!","alert-success"));
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            // error message
            session.setAttribute("message" ,new Message("Contact saving failed ! "+e.getMessage(),"alert-danger"));
        }
        return "user/add-contact-form";
    }

    @GetMapping("/show-contacts")
    public String showContacts(Model model, Principal principal){
        //getting user information
        String email = principal.getName();
        User user = userRepository.getUserByUserName(email);

        List<Contact> contacts = contactRepository.getContactsById(user.getId());

        model.addAttribute("contacts",contacts);

        return "user/show_contacts";
    }
}
