package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FailureException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService service;

    @GetMapping
    public List<Film> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film, BindingResult result, Errors fieldError) throws ValidationException {
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        return service.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film, BindingResult result, Errors fieldError) throws ValidationException, EntityNotFoundException {
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        return service.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @NotNull Integer id, @PathVariable @NotNull Integer userId) throws EntityNotFoundException {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @NotNull Integer id, @PathVariable @NotNull Integer userId) throws EntityNotFoundException, FailureException {
        service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsByPriority(@RequestParam(name = "count", required = false) Integer count) throws FailureException {
        return service.getFilmsByRating(count);
    }

    @GetMapping("/{id}")
    public Film getItem(@PathVariable @NotNull Integer id) throws EntityNotFoundException {
        return service.getItem(id);
    }

    public void deleteHelper() {
        service.deleteHelper();
    }

}
