package com.example.demo.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "EmailAddress cannot be blank")
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Email must conform to be a valid email address pattern!")
    private String emailAddress;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "BVN cannot be blank")
    @Pattern(regexp = "^[0-9]{11}$", message = "BVN must be 11 digits")
    private String bvn;

    @Pattern(regexp = "^[0-9]{11}$", message = "NIN must be 11 digits")
    @NotBlank(message = "NIN cannot be blank")
    private String nin;

}
