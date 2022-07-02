package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        List<User> usersList = new ArrayList<>();
        for (var user : users.values()) {
            usersList.add(user);
        }
        return usersList;
    }

    @PostMapping
    public User postUser(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException {
        log.info("Добавляем пользователя...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка при добавлении пользователя: указана неправильная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя пользователя отсутствует, теперь логин является именем пользователя");
            user.setName(user.getLogin());
        }
        if (user.getId() == null && users.isEmpty()) {
            user.setId(1);
        }
        if (user.getId() == null) {
            List<Integer> userIDs = new ArrayList<>();
            for (Integer id : users.keySet()) {
                userIDs.add(id);
            }
            var minID = Collections.min(userIDs);
            if (minID > 1) {
                minID = 0;
            }
            for (Integer i : userIDs) {
                if (!userIDs.contains(minID + 1)) {
                    user.setId(minID + 1);
                    break;
                } else {
                    minID++;
                }
            }
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");
        return users.get(user.getId());
    }

    @PutMapping
    public User putOrUpdateUser(@RequestBody @Valid User user, BindingResult result, Errors fieldError) throws ValidationException {
        log.info("Обновляем пользователя...");
        if (fieldError.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                log.warn(error.getDefaultMessage());
                throw new ValidationException(error.getDefaultMessage());
            }
        }
        if (users.containsKey(user.getId())) {
            log.info("Пользователь успешно обновлён");
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        log.warn("Ошибка при обновлении пользователя: указан неверный ID");
        throw new RuntimeException("ID пользователя отсутствует в базе данных");
    }

    public void deleteHelper() {
        users.clear();
    }

}
