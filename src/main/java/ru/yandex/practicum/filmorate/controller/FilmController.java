package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        List<Film> filmsList = new ArrayList<>();
        for (var film : films.values()) {
            filmsList.add(film);
        }
        return filmsList;
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film, BindingResult result, Errors fieldError) throws ValidationException {
        log.info("Добавляем фильм...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при добавлении фильма: неверная дата релиза");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка при добавлении фильма: нулевая продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getId() == null && films.isEmpty()) {
            film.setId(1);
        }
        if (film.getId() == null) {
            List<Integer> userIDs = new ArrayList<>();
            for (Integer id : films.keySet()) {
                userIDs.add(id);
            }
            var minID = Collections.min(userIDs);
            if (minID > 1) {
                minID = 0;
            }
            for (Integer i : userIDs) {
                if (!userIDs.contains(minID + 1)) {
                    film.setId(minID + 1);
                    break;
                } else {
                    minID++;
                }
            }
        }
        log.info("Фильм успешно добавлен");
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film, BindingResult result, Errors fieldError) throws ValidationException {
        log.info("Обновляем фильм...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        if (films.containsKey(film.getId())) {
            log.info("Фильм успешно обновлён");
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        log.warn("Ошибка при добавлении фильма: отсутствует ID");
        throw new RuntimeException("ID фильма отсутствует в базе данных");
    }

    public void deleteHelper() {
        films.clear();
    }

}
