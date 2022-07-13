package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public List<User> getAll();

    public User create(User user);

    public User update(User user);

    public User getItem(int id);

    public Map<Integer, User> getUsersMap();

    public void deleteHelper();
}
