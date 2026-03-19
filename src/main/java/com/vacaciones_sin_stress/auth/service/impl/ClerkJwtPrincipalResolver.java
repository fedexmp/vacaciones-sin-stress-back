package com.vacaciones_sin_stress.auth.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.vacaciones_sin_stress.auth.model.AuthenticatedPrincipal;
import com.vacaciones_sin_stress.auth.service.AuthenticatedPrincipalResolver;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Validates the Clerk JWT from the Authorization header against the Clerk JWKS endpoint
 * and extracts the authenticated principal from verified token claims.
 *
 * <p>Disabled when {@code app.auth.clerk.jwks-uri} is not configured.</p>
 */
@Slf4j
@Component
@Order(5)
public class ClerkJwtPrincipalResolver implements AuthenticatedPrincipalResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${app.auth.clerk.jwks-uri:}")
    private String jwksUri;

    @Value("${app.auth.clerk.issuer:}")
    private String issuer;

    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private boolean enabled;

    @PostConstruct
    void init() {
        enabled = StringUtils.hasText(jwksUri);
        if (!enabled) {
            log.info("Clerk JWT validation disabled (app.auth.clerk.jwks-uri not configured)");
            return;
        }

        try {
            JWKSource<SecurityContext> keySource = JWKSourceBuilder
                    .create(new URL(jwksUri))
                    .retrying(true)
                    .build();

            ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
            processor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));

            Set<String> requiredClaims = new HashSet<>();
            requiredClaims.add("sub");
            requiredClaims.add("exp");
            requiredClaims.add("iat");

            DefaultJWTClaimsVerifier<SecurityContext> claimsVerifier;
            if (StringUtils.hasText(issuer)) {
                JWTClaimsSet exactMatch = new JWTClaimsSet.Builder().issuer(issuer).build();
                claimsVerifier = new DefaultJWTClaimsVerifier<>(exactMatch, requiredClaims);
            } else {
                claimsVerifier = new DefaultJWTClaimsVerifier<>(null, requiredClaims);
            }
            processor.setJWTClaimsSetVerifier(claimsVerifier);

            this.jwtProcessor = processor;
            log.info("Clerk JWT validation enabled (JWKS: {}, issuer: {})", jwksUri, issuer);
        } catch (Exception e) {
            log.error("Failed to initialize Clerk JWT processor — JWT validation disabled", e);
            enabled = false;
        }
    }

    @Override
    public Optional<AuthenticatedPrincipal> resolve(HttpServletRequest request) {
        if (!enabled) {
            return Optional.empty();
        }

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            return Optional.empty();
        }

        try {
            JWTClaimsSet claims = jwtProcessor.process(token, null);

            String clerkUserId = claims.getSubject();
            String email = claims.getStringClaim("email");

            if (!StringUtils.hasText(clerkUserId)) {
                log.warn("Clerk JWT missing 'sub' claim");
                return Optional.empty();
            }

            return Optional.of(new AuthenticatedPrincipal(clerkUserId, email));
        } catch (Exception e) {
            log.debug("Clerk JWT validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
