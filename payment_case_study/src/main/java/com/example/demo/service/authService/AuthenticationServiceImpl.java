package com.example.demo.service.authService;

import com.example.demo.auth.JwtService;
import com.example.demo.constants.AccountType;
import com.example.demo.constants.ResponseStatus;
import com.example.demo.constants.StringValues;
import com.example.demo.entity.User;
import com.example.demo.event.PulsarEventPublisher;
import com.example.demo.exceptions.GeneralException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.payload.BaseResponse;
import com.example.demo.payload.request.AuthenticationRequest;
import com.example.demo.payload.request.CreateAccountRequest;
import com.example.demo.payload.request.RegisterRequest;
import com.example.demo.payload.response.AuthenticationResponse;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.event.EventNames.CREATE_ACCOUNT_EVENT_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PulsarEventPublisher publisher;

    @Value("${pulsar.producer.topic-base}")
    private String topicBase;
    @Override
    public BaseResponse<AuthenticationResponse> createUser(HttpServletRequest request, RegisterRequest requestPayload) {
        BaseResponse<AuthenticationResponse> response;
        String url = request.getRequestURL().toString();

        Optional<User> existingUser = userRepository.findUserByEmailAddress(requestPayload.getEmailAddress());
        log.info("existing user --> {}", existingUser);
        if (existingUser.isPresent()) {
            response = new BaseResponse<>();
            response.setCode(ResponseStatus.FAILED.getCode());
            response.setStatus(ResponseStatus.FAILED.getStatus());
            response.setMessage(StringValues.USER_ALREADY_EXIST);
            response.setData(null);
            log.info(StringValues.LOGGER_STRING_POST, url, requestPayload, response);
            return response;
        }

        User newUser = new User();
        BeanUtils.copyProperties(requestPayload, newUser);
        newUser.setPassword(passwordEncoder.encode(requestPayload.getPassword()));
        User saveUser = userRepository.save(newUser);
        log.info("saveStaff ===> {}", saveUser);

        String token = jwtService.generateToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);

        CreateAccountRequest accountRequest = CreateAccountRequest.builder()
                .userId(saveUser.getId())
                .accountTypes(List.of(AccountType.SAVINGS, AccountType.WALLET))
                .bvn(requestPayload.getBvn())
                .nin(requestPayload.getNin())
                .build();
        publisher.publishEvent(
                String.format("%s%s", topicBase, CREATE_ACCOUNT_EVENT_NAME), accountRequest);

        response = new BaseResponse<>();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setMessage(StringValues.USER_RECORD_SAVED);
        response.setData(authenticationResponse(token, refreshToken));
        log.info(StringValues.LOGGER_STRING_POST, url, requestPayload, response);
        return response;
    }

    @Override
    public BaseResponse<AuthenticationResponse> authenticate(HttpServletRequest request, AuthenticationRequest requestPayload) {
        BaseResponse<AuthenticationResponse> response;
        String url = request.getRequestURL().toString();

     try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestPayload.getEmail(),
                    requestPayload.getPassword()));
        } catch (Exception e){
            log.info("Exception --> {}", e.getMessage());
            throw new GeneralException(ResponseStatus.FAILED.getCode(), e.getMessage());
        }

        User user = userRepository.findUserByEmailAddress(requestPayload.getEmail())
                .orElseThrow(() -> new NotFoundException(ResponseStatus.RESOURCE_NOT_FOUND.getCode(),
                        ResponseStatus.RESOURCE_NOT_FOUND.getStatus()));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        response = new BaseResponse<>();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setMessage(StringValues.AUTHENTICATION_SUCCESSFUL);
        response.setData(authenticationResponse(token, refreshToken));
        log.info(StringValues.LOGGER_STRING_POST, url, requestPayload, response);
        return response;
    }

    private AuthenticationResponse authenticationResponse(String jwtToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
