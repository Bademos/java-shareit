package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * TODO Sprint add-controltlers.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @NotBlank
    private String name;
    @Email
    private String email;
}