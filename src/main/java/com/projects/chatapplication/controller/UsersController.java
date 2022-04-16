package com.projects.chatapplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projects.chatapplication.storage.JDBC;

import java.util.List;

@RestController
@CrossOrigin
public class UsersController {

    // Create an account for the username
    // Succeed only when the username entered is not taken
    @GetMapping("/signup/{username}/{password}")
    public ResponseEntity<Void> signup(@PathVariable String username, @PathVariable String password) {
        System.out.println("handling sign up user request: " + username);
        try {
            if (JDBC.getInstance().userExists(username)) {
                System.out.println("user " + username + " already exists!");
                return ResponseEntity.badRequest().build();
            } else {
                JDBC.getInstance().addUser(username, password);
                System.out.println(username + " signed up successfully!");
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }

    // User sign in
    @GetMapping("/signin/{username}/{password}")
    public ResponseEntity<Void> signin(@PathVariable String username, @PathVariable String password) {
        System.out.println("handling sign in user request: " + username);
        try {
            if (JDBC.getInstance().userAuthentication(username, password)) {
                System.out.println(username + " signed in successfully!");
                return ResponseEntity.ok().build();
            } else {
                System.out.println("Invalid username or password!");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }

    // Search for a particular user that the user wants to add as a contact
    // Succeed only if that particular user exists in the system
    @GetMapping("/searchUser/{username}")
    public ResponseEntity<Void> searchUser(@PathVariable String username) {
        System.out.println("Looking for user: " + username);
        try {
            if (JDBC.getInstance().userExists(username)) {
                System.out.println(username + " exists!");
                return ResponseEntity.ok().build();
            } else {
                System.out.println(username + " does not exist!");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }

    // add another user as a contact
    @GetMapping("/addContact/{username}/{friend}")
    public ResponseEntity<Void> addContact(@PathVariable String username, @PathVariable String friend) {
        System.out.println("Adding " + friend + " as a contact to " + username);
        try {
            if (JDBC.getInstance().userExists(friend)) {
                if (JDBC.getInstance().addContact(username, friend)) {
                    System.out.println("Added " + friend + " as a contact to " + username);
                    return ResponseEntity.ok().build();
                } else {
                    System.out.println(friend + " is already added as a contact to " + username);
                    return ResponseEntity.badRequest().build();
                }
            } else {
                System.out.println(username + " does not exist!");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }

    // get all the contacts of an user
    @GetMapping("/getContacts/{username}")
    public ResponseEntity getContacts(@PathVariable String username) {
        System.out.println("getting all contacts of " + username);
        try {
            List<String> contacts = JDBC.getInstance().getContacts(username);
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }

    // get the chat history between a sender and a receiver
    @GetMapping("/getMessageHistory/{sender}/{receiver}")
    public ResponseEntity getContacts(@PathVariable String sender, @PathVariable String receiver) {
        System.out.println("getting chat history between " + sender + " and " + receiver);
        try {
            List<String[]> messageInfo = JDBC.getInstance().getMessages(sender, receiver);
            return new ResponseEntity<>(messageInfo, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Something went wrong with the server...");
        }
        return ResponseEntity.internalServerError().build();
    }
}
