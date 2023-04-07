package com.example.springcardprojectdemo.component;

import com.example.springcardprojectdemo.entity.Role;
import com.example.springcardprojectdemo.entity.Token;
import com.example.springcardprojectdemo.entity.User;
import com.example.springcardprojectdemo.repository.RoleRepository;
import com.example.springcardprojectdemo.repository.TokenRepository;
import com.example.springcardprojectdemo.repository.UserRepository;
import com.example.springcardprojectdemo.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    TokenRepository tokenRepository;

    @Value("${spring.sql.init.mode}")
    private String initMode;

    @Override
    public void run(String... args) {

        String admin_email = "shaxzodemailsender@gmail.com";

        if (initMode.equals("always")) {
            Role admin = roleRepository.save(new Role(1L, "ADMIN"));
            Role user = roleRepository.save(new Role(2L, "USER"));
            Role guest = roleRepository.save(new Role(3L, "GUEST"));

            Token valid_token = new Token();
            valid_token.setToken(jwtProvider.generateToken(admin_email));
            valid_token.setEmail(admin_email);
            valid_token.setLevel("valid");

            Token valided_token = tokenRepository.save(valid_token);

            User user1 = new User("Shaxzod",
                    "Murtozaqulov", admin_email,
                            passwordEncoder.encode("12345"),
                            Arrays.asList(admin, user),
                            Arrays.asList(valided_token),
                            true, true);

            userRepository.save(user1);
        }


    }
}
