package com.example.springcardprojectdemo.controller;

import com.example.springcardprojectdemo.entity.Card;
import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.payload.LoginDto;
import com.example.springcardprojectdemo.payload.RegisterDto;
import com.example.springcardprojectdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto dto) {
        ApiResponse apiResponse = userService.login(dto);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/f_password")
    public HttpEntity<?> forget_password(@RequestParam String email) {
        ApiResponse apiResponse = userService.f_password(email);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/v_f_password")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?> v_forget_password(@RequestParam int v_code) {
        ApiResponse apiResponse = userService.v_f_password(v_code);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/new_password")
    @PreAuthorize(value = "hasRole('USER')")
    public HttpEntity<?> new_password(@RequestParam String password) {
        ApiResponse apiResponse = userService.set_new_password(password);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/hello")
    public HttpEntity<?> hello() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/hello2")
    public HttpEntity<?> hello2() {
        Card card = new Card();
        card.setNumber("8786786579");
        card.setOwner("Sherali");
        card.setBalance(232435);
        return ResponseEntity.ok().body(new ApiResponse("salom", true, card));
    }


    @PostMapping("/register")
    public HttpEntity<?> register(@RequestBody RegisterDto dto) {
        ApiResponse apiResponse = userService.register(dto);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PreAuthorize(value = "hasRole('GUEST')")
    @PostMapping("/verify")
    public HttpEntity<?> verify(@RequestParam Long code) {
        ApiResponse apiResponse = userService.verify(code);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping("/getUsers")
    public HttpEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

}
