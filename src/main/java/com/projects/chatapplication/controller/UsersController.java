package com.projects.chatapplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projects.chatapplication.storage.JDBC;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UsersController {

    // Register the username entered by the user
    // Succeed only when the username entered is not taken
    @GetMapping("/registration/{username}")
    public ResponseEntity<Void> register(@PathVariable String username) {
        System.out.println("handling register user request: " + username);
        try {
            JDBC.getInstance().addUser(username);
        } catch (Exception e) {
            System.out.println("registration failed");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("registration succeeded");
        return ResponseEntity.ok().build();
    }

    // Search for a particular user that the user wants to add as a contact
    // Succeed only if that particular user exists in the system
    @GetMapping("/searchUser/{username}")
    public ResponseEntity<Void> searchUser(@PathVariable String username) {
        System.out.println("Looking for user: " + username);
        try {
            if (JDBC.getInstance().userExists(username)) {
                System.out.println(username + " exists!");
            } else {
                System.out.println(username + " does not exist!");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
