package com.haitao.book.services;

import com.haitao.book.controllers.models.AuthenticatioRequest;
import com.haitao.book.controllers.models.AuthenticationResponse;
import com.haitao.book.controllers.models.RegistrationRequest;
import com.haitao.book.entities.Token;
import com.haitao.book.entities.User;
import com.haitao.book.repositories.RoleRepository;
import com.haitao.book.repositories.TokenRepository;
import com.haitao.book.repositories.UserRepository;
import com.haitao.book.services.mapers.EmailTemplate;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public AuthenticationResponse authenticate(AuthenticatioRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()

                )
        );
        //user is authenticated
        var claims = new HashMap<String, Object>();
        //cast the auth to user. In User class we implemented Principal
        var user = ((User)auth.getPrincipal());
        claims.put("fullname", user.fullName());

        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                    .orElseThrow(()-> new RuntimeException("Invalid token"));

        //if the validation time is expired send again the email validation
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new validation code has been sent");
        }

        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(
                ()-> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
