package com.RedditClone.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.RedditClone.exceptions.SpringRedditException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

@Service
@Data
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration.time}")
	private Long jwtExpirationTime;
	
//    private KeyStore keyStore;
//
//    @PostConstruct
//    public void init() {
//        try {
//            keyStore = KeyStore.getInstance("JKS");
//            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
//            keyStore.load(resourceAsStream, "secret".toCharArray());
//        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
//            throw new SpringRedditException("Exception occurred while loading keystore");
//        }
//
//    }

    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTime)))
                .compact();
    }
    
   
//
//    private PrivateKey getPrivateKey() {
//        try {
//            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
//        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
//            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
//        }
//    }
    
    
    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
        return true;
    }

//    private PublicKey getPublickey() {
//        try {
//            return keyStore.getCertificate("springblog").getPublicKey();
//        } catch (KeyStoreException e) {
//            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
//        }
//    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


	public String generateTokenWithuserName(String username) {
		 return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(Date.from(Instant.now()))
	                .signWith(SignatureAlgorithm.HS512, secret)
	                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTime)))
	                .compact();
	}
}