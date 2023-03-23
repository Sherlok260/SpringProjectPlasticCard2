package com.example.springcardprojectdemo.controller;

import com.example.springcardprojectdemo.repository.CardRepository;
import com.example.springcardprojectdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardRepository cardRepository;

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping("/users")
    public HttpEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping("/cards")
    public HttpEntity<?> getAllCards() {
        return ResponseEntity.ok(cardRepository.findAll());
    }

}
