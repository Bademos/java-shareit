package ru.practicum.shareit.user.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
public class UserDto {
	private int id;
	@NotNull
	private String name;
	@NotNull
	@Email
	private String email;
}