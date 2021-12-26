package com.smartcontactmanager.contactmanager.controllers;

import com.smartcontactmanager.contactmanager.entities.Contact;
import com.smartcontactmanager.contactmanager.entities.ContactImage;
import com.smartcontactmanager.contactmanager.entities.User;
import com.smartcontactmanager.contactmanager.helpers.Message;
import com.smartcontactmanager.contactmanager.repositories.ContactImageRepository;
import com.smartcontactmanager.contactmanager.repositories.ContactRepository;
import com.smartcontactmanager.contactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactImageRepository contactImageRepository;

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
    public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("contactPhoto") MultipartFile[] files, Principal principal, HttpSession session,Model model){
        //System.out.println(contact);
        try{
            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);

            contact.setUser(user);
            Contact result;

            if(files == null){
                System.out.println("File is empty");
                contact.setPhoto("default.png");
                result = contactRepository.save(contact);

                if(result == null){
                    throw new Exception("Contact Saving Failed!");
                }else{
                    System.out.println("contact added successfully!");
                    // success message
                    session.setAttribute("message" ,new Message("Contact Added Successfully!","alert-success"));
                }
            }else{
                if(files.length > 3)
                    throw new Exception("You can Upload maximum 3 files!");
                contact.setPhoto(files[0].getOriginalFilename());
                result = contactRepository.save(contact);

                for (MultipartFile file : files) {

                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Image Uploaded to folder...");

                    ContactImage contactImage = new ContactImage();

                    contactImage.setContactImage(file.getOriginalFilename());
                    //contactImage.setContact(contactRepository.getContactById(result.getId()));
                    contactImage.setContact(result);
                    ContactImage result1 = contactImageRepository.save(contactImage);

                    if(result1 == null){
                        throw new Exception("Image Uploading Failed!");
                    }else{
                        System.out.println("contact added successfully with Images!");
                        // success message
                        session.setAttribute("message" ,new Message("Contact Added Successfully!","alert-success"));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            model.addAttribute("contact",contact);
            // error message
            session.setAttribute("message" ,new Message("Contact saving failed ! "+e.getMessage(),"alert-danger"));
        }
        return "user/add-contact-form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal){
        //getting user information
        String email = principal.getName();
        User user = userRepository.getUserByUserName(email);

        Pageable pageable = PageRequest.of(page,5);

        Page<Contact> contacts = contactRepository.getContactsById(user.getId(),pageable);
        System.out.println(contacts);
        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPage",contacts.getTotalPages());

        return "user/show_contacts";
    }

    @GetMapping("/contact-details/{id}")
    public String contacDetails(@PathVariable("id") int id, Model model){
       Contact contact = contactRepository.findById(id).get();
       model.addAttribute("contact",contact);

       return "user/contact_details";
    }
}
