package com.example.springcardprojectdemo.controller;

import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.payload.CardDto;
import com.example.springcardprojectdemo.payload.TransferDto;
import com.example.springcardprojectdemo.service.CardService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
@CrossOrigin(origins = "*")
public class CardController {

    @Autowired
    CardService cardService;

    @PostMapping("/create")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?> createCard(@RequestBody CardDto dto) {
        ApiResponse apiResponse = cardService.createCard(dto);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/transfer")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?> transferMoney(@RequestBody TransferDto dto) {
        ApiResponse apiResponse = cardService.v_transfer(dto);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/getUserCards")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?> getAllCards() {
        ApiResponse apiResponse = cardService.getAllCardsForUser();
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/checkCardNumber")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?>  checkCardWithNumber(@RequestParam String number) {
        ApiResponse apiResponse = cardService.checkCardNumber(number);
        return ResponseEntity.ok().body(apiResponse);
    }

}