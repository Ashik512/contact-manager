package com.smartcontactmanager.contactmanager.repositories;

import com.smartcontactmanager.contactmanager.entities.Contact;
import com.smartcontactmanager.contactmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("from Contact as c where c.user.id = :userId")
    public Page<Contact> getContactsById(@Param("userId") int userId, Pageable pageable);

  /*  @Query("select c from Contact c where c.id = :id")
    public Contact getContactById(@Param("id") int id);*/
}
