package com.example.springcardprojectdemo.controller;

import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @GetMapping("/sendText")
    public HttpEntity<?> sendText(@RequestParam(name = "email") String email) {
        ApiResponse apiResponse = mailService.sendText(email);
        return ResponseEntity.status(apiResponse.isSuccess()?201:409).body(apiResponse);
    }

    @GetMapping("/sendTextt")
    public HttpEntity<?> sendTextt(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "text") String text) {
        ApiResponse apiResponse = mailService.sendTextt(email,text);
        return ResponseEntity.status(apiResponse.isSuccess()?201:409).body(apiResponse);
    }

    @GetMapping("/sendHtml")
    public HttpEntity<?> sendHtml(@RequestParam String email) {
        ApiResponse apiResponse = mailService.sendHtml(email);
        return ResponseEntity.status(apiResponse.isSuccess()?201:409).body(apiResponse);
    }

    @GetMapping("/activate")
    public HttpEntity<?> activate(@RequestParam String code) {
        return ResponseEntity.ok(code);
    }

    @GetMapping("/sendFile")
    public HttpEntity<?> sendFile(@RequestParam String email) {
        ApiResponse apiResponse = mailService.sendFile(email);
        return ResponseEntity.status(apiResponse.isSuccess()?201:409).body(apiResponse);
    }
}
