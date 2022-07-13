package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    UserStorage storage;

    public void addFriend(Integer id, Integer UserId) throws NotFoundException {
        log.info("Добавляем друга...");
        if (!storage.getUsersMap().containsKey(id)) {
            log.warn("Ошибка при добавлении друга: Передан несуществующий ID пользователя");
            throw new NotFoundException("ID пользователя должен присутствовать в базе данных");
        }
        if (!storage.getUsersMap().containsKey(UserId)) {
            log.warn("Ошибка при добавлении друга: Передан несуществующий ID друга");
            throw new NotFoundException("ID друга должен присутствовать в базе данных");
        }
        log.info("Добавляем друга в список друзей...");
        var user = storage.getItem(id);
        var newSet = user.getFriendsIDs();
        newSet.add(UserId);
        user.setFriendsIDs(newSet);
        storage.update(user);
        var friendUser = storage.getItem(UserId);
        var friendsSet = friendUser.getFriendsIDs();
        friendsSet.add(id);
        friendUser.setFriendsIDs(friendsSet);
        storage.update(friendUser);
        log.info("ID друзей успешно добавлены");
    }

    public void deleteFriend(Integer id, Integer UserId) throws NotFoundException {
        if (!storage.getUsersMap().containsKey(id)) {
            log.warn("Ошибка при удалении друга: Передан несуществующий ID пользователя");
            throw new NotFoundException("ID пользователя должен присутствовать в базе данных");
        }
        if (!storage.getUsersMap().containsKey(UserId)) {
            log.warn("Ошибка при удалении друга: Передан несуществующий ID друга");
            throw new NotFoundException("ID друга должен присутствовать в базе данных");
        }
        log.info("Удаляем друга из списка друзей...");
        var user = storage.getItem(id);
        var newSet = user.getFriendsIDs();
        if (!newSet.contains(UserId)) {
            log.warn("Ошибка при удалении друга: Пользователи не состоят в друзьях между собой");
            throw new NotFoundException("ID друга отсутствует в списке друзей пользователя");
        }
        newSet.remove(UserId);
        user.setFriendsIDs(newSet);
        storage.update(user);
        var friendUser = storage.getItem(UserId);
        var friendsSet = friendUser.getFriendsIDs();
        friendsSet.remove(user.getId());
        friendUser.setFriendsIDs(friendsSet);
        storage.update(friendUser);
        log.info("ID друзей успешно удалены");
    }

    public List<User> getAllFriends(Integer id) throws NotFoundException {
        log.info("Выводим список друзей пользователя...");
        if (!storage.getUsersMap().containsKey(id)) {
            log.warn("Ошибка при выводе списка друзей пользователя: Такого пользователя не существует");
            throw new NotFoundException("Передан неверный ID");
        }
        var user = storage.getItem(id);
        var friendsIDs = user.getFriendsIDs();
        List<User> friends = new ArrayList<User>();
        for (var friend : friendsIDs) {
            friends.add(storage.getItem(friend));
        }
        log.info("Возврат списка друзей произошёл успешно");
        return friends;

    }

    public List<User> getAll() {
        return storage.getAll();
    }

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
        return storage.create(user);
    }

    public User update(User user) throws NotFoundException {
        log.info("Обновляем пользователя...");
        if (!storage.getUsersMap().containsKey(user.getId())) {
            log.warn("Ошибка при обновлении пользователя: указан неверный ID");
            throw new NotFoundException("ID пользователя отсутствует в базе данных");
        }
        return storage.update(user);
    }

    public User getItem(Integer id) throws NotFoundException {
        log.info("Выводим одного пользователя...");
        if (id < 0) {
            log.warn("Ошибка при выводе пользователя: Передан отрицательный ID");
            throw new NotFoundException("Передан отрицательный ID");
        }
        return storage.getItem(id);
    }

    public List<User> getGeneralFriends(Integer id, Integer friendId) throws NotFoundException {
        log.info("Выводим список общих друзей...");
        if (!storage.getUsersMap().containsKey(id)) {
            log.warn("Ошибка при выводе общего списка друзей пользователя: Указан неверный ID пользователя");
            throw new NotFoundException("Передан неверный ID пользователя");
        }
        if (!storage.getUsersMap().containsKey(friendId)) {
            log.warn("Ошибка при выводе списка друзей пользователя: Указан неверный ID друга");
            throw new NotFoundException("Передан неверный ID друга");
        }
        var userFriends = storage.getItem(id).getFriendsIDs();
        var friendFriends = storage.getItem(friendId).getFriendsIDs();
        Set<Integer> generalFriends = new HashSet<Integer>(userFriends);
        generalFriends.retainAll(friendFriends);
        List<User> friends = new ArrayList<>();
        for (var friend : generalFriends) {
            friends.add(storage.getItem(friend));
        }
        log.info("Возврат списка общих друзей произошёл успешно");
        return friends;
    }

    public void deleteHelper() {
        storage.deleteHelper();
    }
}

