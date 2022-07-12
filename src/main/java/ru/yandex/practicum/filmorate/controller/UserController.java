package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    UserService service;

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException {
        log.info("Добавляем пользователя...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        return service.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException, NotFoundException {
        log.info("Обновляем пользователя...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        return service.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws ValidationException, NotFoundException {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws ValidationException, NotFoundException {
        service.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Integer id) throws ValidationException, NotFoundException {
        return service.getAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getGeneralFriends(@PathVariable Integer id, @PathVariable Integer otherId) throws ValidationException, NotFoundException {
        return service.getGeneralFriends(id, otherId);
    }

    @GetMapping("{id}")
    public User getItem(@PathVariable Integer id) throws NotFoundException {
        return service.getItem(id);
    }

    public void deleteHelper() {
        service.deleteHelper();
    }

}
