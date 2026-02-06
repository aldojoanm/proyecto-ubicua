package com.solveria.iamservice.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@ConditionalOnProperty(name = "security.jwt.local.enabled", havingValue = "true")
public class LocalJwtConfig {

    @Bean
    public KeyPair localJwtKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    public RSAKey localJwtRsaKey(LocalJwtProperties properties, KeyPair localJwtKeyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) localJwtKeyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) localJwtKeyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(properties.keyId())
                .build();
    }

    @Bean
    public JWKSet localJwkSet(RSAKey localJwtRsaKey) {
        return new JWKSet(localJwtRsaKey);
    }

    @Bean
    public JwtEncoder localJwtEncoder(JWKSet localJwkSet) {
        JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource = new ImmutableJWKSet<>(localJwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder localJwtDecoder(RSAKey localJwtRsaKey) throws com.nimbusds.jose.JOSEException {
        RSAPublicKey publicKey = localJwtRsaKey.toRSAPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
