package com.example.Trello_Mini.service.User;
import com.example.Trello_Mini.dto.request.AuthenticationRequest;
import com.example.Trello_Mini.dto.request.IntrospectRequest;
import com.example.Trello_Mini.dto.request.LogoutRequest;
import com.example.Trello_Mini.dto.request.RefreshRequest;
import com.example.Trello_Mini.dto.request.GoogleLoginRequest;
import com.example.Trello_Mini.dto.response.AuthenticationResponse;
import com.example.Trello_Mini.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse authenticateWithGoogle(GoogleLoginRequest request) throws Exception;
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
}
