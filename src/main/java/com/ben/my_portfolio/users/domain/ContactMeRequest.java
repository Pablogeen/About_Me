package com.ben.my_portfolio.users.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactMeRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[+]?[0-9\\s-]{7,20}$",
            message = "Enter a valid phone number")
    private String phoneNumber;

    @NotBlank(message = "Reason for contact is required")
    @Size(min = 3, max = 100,
            message = "Reason must be between 3 and 100 characters")
    private String reasonForContact;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 5000,
            message = "Message must be between 10 and 5000 characters")
    private String message;

}
