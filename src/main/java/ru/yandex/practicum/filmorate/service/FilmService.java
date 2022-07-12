package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FailureException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    @Autowired
    FilmStorage storage;
    @Autowired
    UserStorage userStorage;

    public void addLike(Integer filmId, Integer userId) throws ValidationException, NotFoundException {
        log.info("Добавляем лайк...");
        if (!storage.getFilmsMap().containsKey(filmId)) {
            log.warn("Ошибка при добавлении лайка: Передан несуществующий ID фильма");
            throw new NotFoundException("ID фильма должен присутствовать в базе данных");
        }
        if (!userStorage.getUsersMap().containsKey(userId)) {
            log.warn("Ошибка при добавлении лайка: Указан неверный ID пользователя");
            throw new NotFoundException("Передан неверный ID пользователя");
        }
        var film = storage.getItem(filmId);
        var likeIDs = film.getWhoLikedIDs();
        likeIDs.add(userId);
        film.setWhoLikedIDs(likeIDs);
        storage.update(film);
        log.info("Лайк успешно добавлен");
    }

    public void removeLike(Integer filmId, Integer userId) throws ValidationException, NotFoundException, FailureException {
        log.info("Удаляем лайк...");
        if (!storage.getFilmsMap().containsKey(filmId)) {
            log.warn("Ошибка при удалении лайка: Передан несуществующий ID фильма");
            throw new NotFoundException("ID фильма должен присутствовать в базе данных");
        }
        if (!userStorage.getUsersMap().containsKey(userId)) {
            log.warn("Ошибка при удалении лайка: Указан неверный ID пользователя");
            throw new NotFoundException("Передан неверный ID пользователя");
        }
        var film = storage.getItem(filmId);
        var likeIDs = film.getWhoLikedIDs();
        if (likeIDs.isEmpty()) {
            log.warn("Ошибка при удалении лайка: Лайки у фильма отсутствуют");
            throw new FailureException("Лайки у фильма отсутствуют");
        }
        likeIDs.remove(userId);
        film.setWhoLikedIDs(likeIDs);
        storage.update(film);
        log.info("Лайк успешно удалён");
    }

    public List<Film> getFilmsByRating(Integer count) throws FailureException {
        log.info("Выводим список фильмов по рейтингу...");
        var sortedList = storage.getAll();
        if (sortedList == null) {
            log.warn("Осуществление запроса при пустом списке фильмов");
            throw new FailureException("Список фильмов пуст");
        }
        Collections.sort(sortedList, Comparator.comparingInt(o -> o.getWhoLikedIDs().size()));
        Collections.reverse(sortedList);
        List<Film> resultList = null;
        if (count == null || count == 0) {
            if (sortedList.size() > 10) {
                resultList = sortedList.subList(0, 10);
            } else {
                resultList = sortedList.subList(0, sortedList.size());
            }
        } else if (count > sortedList.size()) {
            resultList = sortedList.subList(0, sortedList.size());
        } else {
            resultList = sortedList.subList(0, count);
        }
        return resultList;
    }

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film create(Film film) throws ValidationException {
        return storage.create(film);
    }

    public Film update(Film film) throws ValidationException, NotFoundException {
        return storage.update(film);
    }

    public Film getItem(Integer id) throws NotFoundException {
        return storage.getItem(id);
    }


}
