package com.example.springcardprojectdemo.service;

import com.example.springcardprojectdemo.entity.User;
import com.example.springcardprojectdemo.entity.Verify;
import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.payload.LoginDto;
import com.example.springcardprojectdemo.payload.RegisterDto;
import com.example.springcardprojectdemo.repository.RoleRepository;
import com.example.springcardprojectdemo.repository.UserRepository;
import com.example.springcardprojectdemo.repository.VerifyRepository;
import com.example.springcardprojectdemo.security.JwtFilter;
import com.example.springcardprojectdemo.security.JwtProvider;
import com.example.springcardprojectdemo.utills.CommonUtills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VerifyRepository verifyRepository;

    @Autowired
    MailService mailService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    public ApiResponse login(LoginDto dto) {
        System.out.println(dto);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()));
        String token = jwtProvider.generateToken(dto.getEmail());
        System.out.println(token);
        return new ApiResponse("foydalanuvchi", true, token);
    }

    public ApiResponse f_password(String email) {
        try {
            int new_verify_code = CommonUtills.generateCode();
            verifyRepository.save(new Verify((long) new_verify_code, email));
            mailService.sendTextt(email, String.valueOf(new_verify_code));
            return new ApiResponse("recovery code is sending", true, jwtProvider.generateToken(email));
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse v_f_password(long v_code) {
        String email = JwtFilter.getEmailWithToken;
        if (verifyRepository.findByEmail(email).get().getVerifyCode().equals(v_code)) {
            verifyRepository.deleteByEmail(email);
            return new ApiResponse("Recovery code is true. Please enter new code for your account", true);
        } else return new ApiResponse("recovery code is incorrect", false);
    }

    public ApiResponse set_new_password(String new_password) {
        try {
            String email = JwtFilter.getEmailWithToken;
            userRepository.updatePassword(email, passwordEncoder.encode(new_password));
            return new ApiResponse("password replace is successfully", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse register(RegisterDto dto) {
        String token = jwtProvider.generateToken(dto.getEmail());
        System.out.println(token);
        Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
        Long code = (long) CommonUtills.generateCode();
        User new_user = new User(dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                roleRepository.findByName("GUEST").get(),
                true, false);

        if(byEmail.isPresent()) {
            if (byEmail.get().isVerified()) {
                return new ApiResponse("This email is already registered", false);
            } else {
                userRepository.deleteFromRole(userRepository.findByEmail(dto.getEmail()).get().getId());
                userRepository.deleteByEmail(dto.getEmail());
                userRepository.save(new_user);
                verifyRepository.deleteByEmail(dto.getEmail());
                verifyRepository.save(new Verify(code, dto.getEmail()));
                mailService.sendTextt(dto.getEmail(), code.toString());
                return new ApiResponse("Code is sending", true, token);
            }
        } else {
            System.out.println(new_user);

            userRepository.save(new_user);
            verifyRepository.save(new Verify(code, dto.getEmail()));
            mailService.sendTextt(dto.getEmail(), code.toString());
            return new ApiResponse("Code is sending for new user", true, token);
        }
    }

    public ApiResponse verify(Long code) {
        String getEmailWithToken = JwtFilter.getEmailWithToken;
        if (verifyRepository.findByEmail(getEmailWithToken).get().getVerifyCode().equals(code)) {
            userRepository.updateVerifyAndRoleWithEmail(userRepository.findByEmail(getEmailWithToken).get().getId());
            userRepository.updateVerifyTrue(getEmailWithToken);
            verifyRepository.deleteByEmail(getEmailWithToken);
            return new ApiResponse("Verification is success", true);
        } else return new ApiResponse("verify code is incorrect", false);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
