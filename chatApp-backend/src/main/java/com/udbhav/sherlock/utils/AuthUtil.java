package com.udbhav.sherlock.utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

public class AuthUtil {
    private static final String BASE64_SECRET = "WU9VUl9CQVNFNjRfRU5DT0RFRF9TRUNSRVRfS0VZX0hFUkU=";

    private static final Key key = new SecretKeySpec(
        Base64.getDecoder().decode(BASE64_SECRET),
        SignatureAlgorithm.HS256.getJcaName()
    );
    private static final long EXPIRATION_TIME_MS = 86400000; // 1 day

    public static String generateToken(String userId) {
        Logger.info("Generating token for userId: " + userId);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key)
                .compact();
    }

    public static String validateTokenAndGetUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
