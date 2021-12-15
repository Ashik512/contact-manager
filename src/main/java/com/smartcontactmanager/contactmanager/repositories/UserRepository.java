package com.smartcontactmanager.contactmanager.repositories;

import com.smartcontactmanager.contactmanager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
