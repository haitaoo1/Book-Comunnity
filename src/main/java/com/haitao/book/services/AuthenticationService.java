package com.haitao.book.services;

import com.haitao.book.controllers.models.RegistrationRequest;
import com.haitao.book.entities.Token;
import com.haitao.book.entities.User;
import com.haitao.book.repositories.RoleRepository;
import com.haitao.book.repositories.TokenRepository;
import com.haitao.book.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByname("USER")
                // to do better exception handling
                .orElseThrow(() -> new IllegalStateException("Role User was not initialized"));
        
        //register user with the request data
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        //send EMAIL to enable user
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        //send email
        emailService.sendEmail(user.getEmail(), user.getUsername(),
                                EmailTemplate.ACTIVATE_ACCOUNT,
                                activationUrl,
                                newToken,
                                "Account Activation"
                );


    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generateToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generateToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generateToken;
    }

    //generate a 6 length ActivationCode
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        //random number is crypted
        SecureRandom secureRandom = new SecureRandom();
        //random index 0...9
        for (int i = 0; i < length ; i++) {
            //nextInt() generate a int with a length of 10 (0...9)
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
