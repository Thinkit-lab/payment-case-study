package com.example.demo.controller;

import com.example.demo.payload.BaseResponse;
import com.example.demo.payload.request.AuthenticationRequest;
import com.example.demo.payload.request.RegisterRequest;
import com.example.demo.payload.response.AuthenticationResponse;
import com.example.demo.service.authService.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register user", description = "Profile user payment service")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AuthenticationResponse>> createUser(
            HttpServletRequest request, @Valid @RequestBody RegisterRequest requestPayload) {
        return ResponseEntity.ok(authenticationService.createUser(request, requestPayload));
    }

    @Operation(summary = "Authenticate user", description = "Authenticate user, return accessToken and refreshToken")
    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<AuthenticationResponse>> authenticate(
            HttpServletRequest request, @Valid @RequestBody AuthenticationRequest requestPayload) {
        return ResponseEntity.ok(authenticationService.authenticate(request, requestPayload));
    }
}
