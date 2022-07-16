package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();


    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (user.getId() == null && users.isEmpty()) {
            user.setId(1);
        }
        if (user.getId() == null) {
            List<Integer> userIDs = new ArrayList<>(users.keySet());
            user.setId(Collections.max(userIDs) + 1);
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        log.info("Пользователь успешно обновлён");
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getItem(int id) {
        log.info("Вывод пользователя произошёл успешно");
        return users.get(id);
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    public void deleteHelper() {
        users.clear();
    }
}
