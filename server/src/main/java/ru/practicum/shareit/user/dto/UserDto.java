package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @NotBlank
    @Email
    String email;

    @JsonCreator
    public UserDto(@JsonProperty("id") Long id,
                   @JsonProperty("name") String name,
                   @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}