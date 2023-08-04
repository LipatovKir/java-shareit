package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @Email
    private String email;

    @JsonCreator
    public UserDto(@JsonProperty("id") Long id,
                   @JsonProperty("name") String name,
                   @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}