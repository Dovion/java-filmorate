package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    //@NotNull(message = "Ошибка при создании фильма: ID фильма не может быть пустым")
    private Integer id;
    @NotBlank(message = "Ошибка при создании фильма: название фильма не может быть пустым")
    private String name;
    @NotBlank(message = "Ошибка при создании фильма: описание фильма не может быть пустым")
    @Size(max = 200, message = "Ошибка при создании фильма: максимальный размер описания фильма - 200 символов")
    private String description;
    @NotNull(message = "Ошибка при создании фильма: дата релиза фильма не может быть пустой")
    private LocalDate releaseDate;
    @NotNull(message = "Ошибка при создании фильма: продолжительность фильма не может быть пустой")
    private Integer duration;

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
