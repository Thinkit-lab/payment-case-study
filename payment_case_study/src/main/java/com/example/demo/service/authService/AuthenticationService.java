package com.example.demo.service.authService;


import com.example.demo.payload.BaseResponse;
import com.example.demo.payload.request.AuthenticationRequest;
import com.example.demo.payload.request.RegisterRequest;
import com.example.demo.payload.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    BaseResponse<AuthenticationResponse> createUser(HttpServletRequest request, RegisterRequest requestPayload);

    BaseResponse<AuthenticationResponse> authenticate(HttpServletRequest request, AuthenticationRequest requestPayload);
}
