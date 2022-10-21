package com.RedditClone.service;


import static java.time.Instant.now;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RedditClone.dto.AuthenticationResponse;
import com.RedditClone.dto.LoginRequest;
import com.RedditClone.dto.RefreshTokenRequest;
import com.RedditClone.dto.RegisterRequest;
import com.RedditClone.entity.NotificationEmail;
import com.RedditClone.entity.Role;
import com.RedditClone.entity.User;
import com.RedditClone.entity.VerificationToken;
import com.RedditClone.exceptions.SpringRedditException;
import com.RedditClone.repository.RoleRepository;
import com.RedditClone.repository.UserRepository;
import com.RedditClone.repository.VerificationTokenRepository;
import com.RedditClone.security.JwtProvider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

	   private final UserRepository userRepository;
	    private final PasswordEncoder passwordEncoder;
	    private final JwtProvider jwtProvider;
	    private final AuthenticationManager authenticationManager;
	    private final VerificationTokenRepository verificationTokenRepository;
	    private final MailContentBuilder mailContentBuilder;
	    private final MailService mailService;
	    private final RoleRepository roleRepository;
	    private final RefreshTokenService refreshTokenService;
	   
	    

	    @Transactional
	    public void signup(RegisterRequest registerRequest) {
	        User user = new User();
	        user.setUsername(registerRequest.getUsername());
	        user.setEmail(registerRequest.getEmail());
	        user.setPassword(encodePassword(registerRequest.getPassword()));
	        user.setCreated(now());
	        Role role = roleRepository.findById((long)1).get();
	        user.setRole(role);
	        
	        user.setEnabled(false);

	        userRepository.save(user);
	        String ACTIVATION_EMAIL = "http://localhost:8080/api/auth/accountVerification";

	        String token = generateVerificationToken(user);
	        String message = mailContentBuilder.build("Thank you for signing up to Spring Reddit, please click on the below url to activate your account : "
	                + ACTIVATION_EMAIL + "/" + token);

	        mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(), message));
	    }

	    public AuthenticationResponse login(LoginRequest loginRequest) {
	        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
	                loginRequest.getPassword()));
	        SecurityContextHolder.getContext().setAuthentication(authenticate);
	        String authenticationToken = jwtProvider.generateToken(authenticate);
	        return  AuthenticationResponse.builder()
	        		.authenticationToken(authenticationToken)
	        		.username(loginRequest.getUsername())
	        		.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTime()))
	        		.refreshToken(refreshTokenService.generateRefreshToken().getToken())
	        		 .build();
	    }
	    
	    private String generateVerificationToken(User user) {
	        String token = UUID.randomUUID().toString();
	        VerificationToken verificationToken = new VerificationToken();
	        verificationToken.setToken(token);
	        verificationToken.setUser(user);
	        verificationTokenRepository.save(verificationToken);
	        return token;
	    }

	    private String encodePassword(String password) {
	        return passwordEncoder.encode(password);
	    }

	    public void verifyAccount(String token) {
	        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
	        verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Invalid Token"));
	        fetchUserAndEnable(verificationTokenOptional.get());
	    }

	    @Transactional
	    private void fetchUserAndEnable(VerificationToken verificationToken) {
	        String username = verificationToken.getUser().getUsername();
	        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User Not Found with id - " + username));
	        user.setEnabled(true);
	        userRepository.save(user);
	    }
	    
	    @Transactional(readOnly = true)
	    User getCurrentUser() {
	        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
	                getContext().getAuthentication().getPrincipal();
	        return userRepository.findByUsername(principal.getUsername())
	                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
	    }

		public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
			refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
			
			String token = jwtProvider.generateTokenWithuserName(refreshTokenRequest.getUsername());
			
			 return  AuthenticationResponse.builder()
		        		.authenticationToken(token)
		        		.username(refreshTokenRequest.getUsername())
		        		.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTime()))
		        		.refreshToken(refreshTokenRequest.getRefreshToken())
		        		.build();
		}
}