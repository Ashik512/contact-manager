package com.smartcontactmanager.contactmanager.repositories;

import com.smartcontactmanager.contactmanager.entities.ContactImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactImageRepository extends JpaRepository<ContactImage,Integer> {
}
