package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    @NotNull(message = "Ошибка при создании пользователя: ID пользователя не может быть пустым")
    private Integer id;
    @NotBlank(message = "Ошибка при создании пользователя: Email пользователя не может быть пустым")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$", message = "Ошибка при создании пользователя: Email пользователя не соответствует нужному формату")
    private String email;
    @NotBlank(message = "Ошибка при создании пользователя: логин пользователя не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Ошибка при создании пользователя: логин пользователя не может содержать пробелы")
    private String login;
    private String name;
    @NotNull(message = "Ошибка при создании пользователя: дата рождения пользователя не может быть пустой")
    private LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
