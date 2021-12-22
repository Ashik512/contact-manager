package com.smartcontactmanager.contactmanager.repositories;

import com.smartcontactmanager.contactmanager.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("from Contact as c where c.user.id = :userId")
    public List<Contact> getContactsById(@Param("userId") int userId);
}
