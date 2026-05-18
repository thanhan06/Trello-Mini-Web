package com.example.Trello_Mini.service.User;
import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.AuthenticationRequest;
import com.example.Trello_Mini.dto.request.IntrospectRequest;
import com.example.Trello_Mini.dto.request.LogoutRequest;
import com.example.Trello_Mini.dto.request.RefreshRequest;
import com.example.Trello_Mini.dto.request.GoogleLoginRequest;
import com.example.Trello_Mini.dto.response.AuthenticationResponse;
import com.example.Trello_Mini.dto.response.IntrospectResponse;
import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.repository.User.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service("authenticationService")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    StringRedisTemplate redisTemplate;
    PasswordEncoder passwordEncoder;
    @NonFinal
    @Value("${jwt.signerKey:quangvuong_signer_key_must_be_long_enough_to_be_secure_1234567890}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${google.client.id}")
    protected String GOOGLE_CLIENT_ID;

    @NonFinal
    @Value("${google.client.secret}")
    protected String GOOGLE_CLIENT_SECRET;

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(signedJWT.getJWTClaimsSet().getJWTID()))) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.token();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (ApiException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }
    
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationResponse authenticateWithGoogle(GoogleLoginRequest request) throws Exception {
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(), 
                new GsonFactory(),
                GOOGLE_CLIENT_ID, 
                GOOGLE_CLIENT_SECRET, 
                request.getCode(), 
                "postmessage" // Typical redirect_uri for Single Page Applications
        ).execute();
        
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new UserEntity();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setPassword(""); 
                user.setRole("USER");
                user = userRepository.save(user);
            }

            var token = generateToken(user);
            var refreshToken = generateRefreshToken(user);
            return AuthenticationResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();
        } else {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
    }
    
    private String generateToken(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("trello-mini.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("trello-mini.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getRefreshToken());
        
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        long remainingTime = expiryTime.getTime() - System.currentTimeMillis();
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(jit, "invalidated", remainingTime, TimeUnit.MILLISECONDS);
        }

        String email = signToken.getJWTClaimsSet().getSubject();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        var refreshToken = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getAccessToken());
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            long remainingTime = expiryTime.getTime() - System.currentTimeMillis();
            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(jit, "invalidated", remainingTime, TimeUnit.MILLISECONDS);
            }
        } catch (ApiException e) {
        }
        
        try {
            var signRefreshToken = verifyToken(request.getRefreshToken());
            String refreshJit = signRefreshToken.getJWTClaimsSet().getJWTID();
            Date refreshExpiryTime = signRefreshToken.getJWTClaimsSet().getExpirationTime();

            long remainingRefreshTime = refreshExpiryTime.getTime() - System.currentTimeMillis();
            if (remainingRefreshTime > 0) {
                redisTemplate.opsForValue().set(refreshJit, "invalidated", remainingRefreshTime, TimeUnit.MILLISECONDS);
            }
        } catch (ApiException e) {
        }
    }

}
