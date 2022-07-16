package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException {
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
    public User update(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException, EntityNotFoundException {
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
    public void addFriend(@PathVariable @NotNull Integer id, @PathVariable @NotNull Integer friendId) throws EntityNotFoundException {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable @NotNull Integer id, @PathVariable @NotNull Integer friendId) throws EntityNotFoundException {
        service.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable @NotNull Integer id) throws EntityNotFoundException {
        return service.getAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getGeneralFriends(@PathVariable @NotNull Integer id, @PathVariable @NotNull Integer otherId) throws EntityNotFoundException {
        return service.getGeneralFriends(id, otherId);
    }

    @GetMapping("{id}")
    public User getItem(@PathVariable @NotNull Integer id) throws EntityNotFoundException {
        return service.getItem(id);
    }

    public void deleteHelper() {
        service.deleteHelper();
    }

}
