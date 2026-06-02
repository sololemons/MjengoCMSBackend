package com.authenticationservice.authentication.utilis;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

public class GenerateSystemToken {

    public static void main(String[] args) {

        // 1. ⚠️ CRITICAL: Paste the EXACT secret key from your Auth Service's application.properties here!
        // If this doesn't match the Auth Service key, the Gateway will reject the token.
        String myAuthServiceSecretKey = "PA2PXn8s+kbn0gxkbzNfG+2ZcSNFfjIyh6P+XHHMB5g=";

        // 2. The identifier for your system account
        String systemAccountEmail = "solomonndimu75@gmail.com";

        // 3. Generate the token (Notice there is NO .setExpiration() method here)
        String permanentToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(systemAccountEmail)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(myAuthServiceSecretKey), SignatureAlgorithm.HS256)
                .compact();

        // 4. Print it out nicely to the IntelliJ console
        System.out.println("\n========================================================");
        System.out.println("🚀 YOUR PERMANENT SYSTEM JWT FOR STOREKEEPER:");
        System.out.println("========================================================");
        System.out.println(permanentToken);
        System.out.println("========================================================\n");
    }

    // Helper method to decode your secret key (matches your Auth Service logic)
    private static Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}