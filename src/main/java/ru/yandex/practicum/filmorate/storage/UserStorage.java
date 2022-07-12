package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public List<User> getAll();

    public User create(User user) throws ValidationException;

    public User update(User user) throws ValidationException, NotFoundException;

    public User getItem(Integer id) throws NotFoundException;

    public Map<Integer, User> getUsersMap();
}
