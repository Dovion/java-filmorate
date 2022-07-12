package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();


    @Override
    public List<User> getAll() {
        List<User> usersList = new ArrayList<>();
        for (var user : users.values()) {
            usersList.add(user);
        }
        return usersList;
    }

    @Override
    public User create(User user) throws ValidationException {
        log.info("Добавляем пользователя...");
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

    @Override
    public User update(User user) throws NotFoundException {
        log.info("Обновляем пользователя...");
        if (users.containsKey(user.getId())) {
            log.info("Пользователь успешно обновлён");
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        log.warn("Ошибка при обновлении пользователя: указан неверный ID");
        throw new NotFoundException("ID пользователя отсутствует в базе данных");
    }

    @Override
    public User getItem(Integer id) throws NotFoundException {
        log.info("Выводим одного пользователя...");
        if(id < 0){
            log.warn("Ошибка при выводе пользователя: Передан отрицательный ID");
            throw new NotFoundException("Передан отрицательный ID");
        }
        try {
            log.info("Вывод пользователя произошёл успешно");
            return users.get(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    public void deleteHelper() {
        users.clear();
    }
}
