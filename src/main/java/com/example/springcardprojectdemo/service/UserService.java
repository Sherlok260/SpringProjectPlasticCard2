package com.example.springcardprojectdemo.service;

import com.example.springcardprojectdemo.entity.Token;
import com.example.springcardprojectdemo.entity.User;
import com.example.springcardprojectdemo.entity.Verify;
import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.payload.LoginDto;
import com.example.springcardprojectdemo.payload.RegisterDto;
import com.example.springcardprojectdemo.repository.RoleRepository;
import com.example.springcardprojectdemo.repository.TokenRepository;
import com.example.springcardprojectdemo.repository.UserRepository;
import com.example.springcardprojectdemo.repository.VerifyRepository;
import com.example.springcardprojectdemo.security.JwtFilter2;
import com.example.springcardprojectdemo.security.JwtProvider;
import com.example.springcardprojectdemo.utills.CommonUtills;
import freemarker.log.Logger;
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
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    MailService mailService;

    @Autowired
    VerifyRepository verifyRepository;

    public ApiResponse login(LoginDto dto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()));
            List<Token> token = userRepository.findByEmail(dto.getEmail()).get().getTokens();
            for (Token t: token) {
                if (t.getLevel().equals("valid")) {
                    return new ApiResponse("success", true, t.getToken());
                }
            }
            return new ApiResponse("User name or password incorrect", false);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("User name or password incorrect", false);
        }
    }

    public ApiResponse register(RegisterDto dto) {

        try {

            Token temporary_token = new Token(
                    jwtProvider.generateTokenWithTime(dto.getEmail()),
                    dto.getEmail(), "temporary");

            User new_user = new User(dto.getFirstName(),
                    dto.getLastName(), dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()),
                    List.of(roleRepository.findByName("GUEST").get()),
                    true, false);

            Optional<User> user_in_db = userRepository.findByEmail(dto.getEmail());
            if (user_in_db.isPresent()) {
                if (user_in_db.get().isVerified()) {
                    return new ApiResponse("This user is already registered", false);
                } else {
                    Optional<Verify> verify_in_db = verifyRepository.findByEmail(dto.getEmail());
                    if (verify_in_db.isPresent()) {
                        verifyRepository.deleteByEmail(dto.getEmail());
                    }
                    Optional<List<Token>> token_in_db = tokenRepository.findByEmail(dto.getEmail());
                    if (token_in_db.isPresent()) {
                        userRepository.deleteTokenIdFromUser(userRepository.getUserIdByEmail(dto.getEmail()));
                        tokenRepository.deleteTemporaryTokenByEmail(dto.getEmail());
                    }
                    userRepository.deleteByEmail(dto.getEmail());
                    return save_register_info(new_user, temporary_token, dto);
                }
            } else {
                return save_register_info(new_user, temporary_token, dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse save_register_info(User new_user, Token temporary_token, RegisterDto dto) {
        try {

            new_user.setTokens(List.of(tokenRepository.save(temporary_token)));
            userRepository.save(new_user);
            Long verify_code = (long) CommonUtills.generateCode();
            Verify verify = new Verify(verify_code, dto.getEmail());
            verifyRepository.save(verify);
            mailService.sendTextt(dto.getEmail(), String.valueOf(verify_code));

            return new ApiResponse("Verify code is sending", true, temporary_token.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse f_password(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            if(user.get().isVerified()) {
                int code = CommonUtills.generateCode();
                verifyRepository.save(new Verify((long) code, email));
                mailService.sendTextt(user.get().getEmail(), "Code to restore your account: " + code);
                String token = jwtProvider.generateToken(email);
                Token temporary_token = tokenRepository.save(new Token(token, email, "temporary"));
                user.get().addToken(temporary_token);
                userRepository.save(user.get());
                return new ApiResponse("forget password sending", true, temporary_token.getToken());
            } else {
                return new ApiResponse("forget password sending", true, "account not verified");
            }
        } else {
            return new ApiResponse("forget password sending", true, "account not found");
        }
    }

    public ApiResponse v_f_password(long v_code) {
        String token = JwtFilter2.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("temporary")) {
            String email = JwtFilter2.getEmailWithToken;
            Optional<Verify> verify_details = verifyRepository.findByEmail(email);
            if(verify_details.isPresent()) {
                if(verify_details.get().getVerifyCode().equals(v_code)) {
                    return new ApiResponse("verify true", true);
                } else {
                    return new ApiResponse("verify code is incorrect", false);
                }
            } else {
                return new ApiResponse("something wrong", false);
            }
        } else {
            return new ApiResponse("token failed", false);
        }
    }

    public ApiResponse set_new_password(String new_password, long v_code) {
        String token = JwtFilter2.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("temporary")) {
            String email = JwtFilter2.getEmailWithToken;
            Optional<Verify> verify_details = verifyRepository.findByEmail(email);
            if(verify_details.isPresent()) {
                if(verify_details.get().getVerifyCode().equals(v_code)) {
                    User user = userRepository.findByEmail(email).get();
                    userRepository.deleteTokenIdFromUser(user.getId());
                    tokenRepository.deleteTemporaryTokenByToken(token);
                    verifyRepository.deleteByEmail(email);
                    user.setPassword(passwordEncoder.encode(new_password));
                    userRepository.save(user);
                    return new ApiResponse("Password successfully changed", true);
                } else {
                    return new ApiResponse("something wrong", false);
                }
            } else {
                return new ApiResponse("something wrong", false);
            }
        } else {
            return new ApiResponse("token failed", false);
        }
    }

    public ApiResponse verify(Long code) {

        String token = JwtFilter2.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("temporary")) {
            String email = JwtFilter2.getEmailWithToken;
            Optional<Verify> verify_details = verifyRepository.findByEmail(email);
            if(verify_details.isPresent()) {
                if(verify_details.get().getVerifyCode().equals(code)) {
                    Long user_id = userRepository.getUserIdByEmail(email);
                    userRepository.deleteTokenIdFromUser(userRepository.getUserIdByEmail(email));
                    tokenRepository.deleteTemporaryTokenByEmail(email);
                    Token valid_token = tokenRepository.save(new Token(jwtProvider.generateToken(email), email, "valid"));
                    userRepository.setTokenIdForUser(user_id, valid_token.getId());
                    userRepository.updateVerifyAndRoleWithEmail(user_id);
                    userRepository.updateVerifyTrue(email);
                    verifyRepository.deleteByEmail(email);
                    return new ApiResponse("Verification is success", true);
                } else {
                    return new ApiResponse("Verify code is incorrect", false);
                }
            } else {
                Logger logger = Logger.getLogger(UserService.class.getName());
                logger.info("verify details: \n" + verify_details.get() + "\n");
                return new ApiResponse("verify db something wrong", false);
            }
        } else {
            return new ApiResponse("token failed", false);
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
