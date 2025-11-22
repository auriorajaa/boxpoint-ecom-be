/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dev.boxpoint.repository;

import com.dev.boxpoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author mraur
 */
public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmail(String email);

}
