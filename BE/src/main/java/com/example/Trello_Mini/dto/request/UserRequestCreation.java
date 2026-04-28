package com.example.Trello_Mini.dto.request;

import com.example.Trello_Mini.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserRequestCreation {
    String name;
    @Email(message = "Email should be valid")
    String email;
    @Size(min = 8, max = 20, message = "INVALID_PASSWORD")
    String password;
    String role;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
}
