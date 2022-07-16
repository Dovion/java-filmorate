package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FailureException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
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

    public void addLike(int filmId, int userId) throws EntityNotFoundException {
        log.info("Добавляем лайк...");
        if (!storage.getFilmsMap().containsKey(filmId)) {
            log.warn("Ошибка при добавлении лайка: Передан несуществующий ID фильма");
            throw new EntityNotFoundException("ID фильма должен присутствовать в базе данных");
        }
        if (!userStorage.getUsersMap().containsKey(userId)) {
            log.warn("Ошибка при добавлении лайка: Указан неверный ID пользователя");
            throw new EntityNotFoundException("Передан неверный ID пользователя");
        }
        var film = storage.getItem(filmId);
        var likeIDs = film.getWhoLikedIDs();
        likeIDs.add(userId);
        film.setWhoLikedIDs(likeIDs);
        storage.update(film);
        log.info("Лайк успешно добавлен");
    }

    public void removeLike(int filmId, int userId) throws EntityNotFoundException, FailureException {
        log.info("Удаляем лайк...");
        if (!storage.getFilmsMap().containsKey(filmId)) {
            log.warn("Ошибка при удалении лайка: Передан несуществующий ID фильма");
            throw new EntityNotFoundException("ID фильма должен присутствовать в базе данных");
        }
        if (!userStorage.getUsersMap().containsKey(userId)) {
            log.warn("Ошибка при удалении лайка: Указан неверный ID пользователя");
            throw new EntityNotFoundException("Передан неверный ID пользователя");
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
        List<Film> resultList;
        if (count == null || count <= 0) {
            resultList = sortedList.subList(0, Math.min(10, sortedList.size()));
        } else {
            resultList = sortedList.subList(0, Math.min(count, sortedList.size()));
        }
        return resultList;
    }

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film create(Film film) throws ValidationException {
        log.info("Добавляем фильм...");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при добавлении фильма: неверная дата релиза");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        return storage.create(film);
    }

    public Film update(Film film) throws EntityNotFoundException {
        log.info("Обновляем фильм...");
        if (!storage.getFilmsMap().containsKey(film.getId())) {
            log.warn("Ошибка при добавлении фильма: отсутствует ID");
            throw new EntityNotFoundException("ID фильма отсутствует в базе данных");
        }
        return storage.update(film);
    }

    public Film getItem(int id) throws EntityNotFoundException {
        log.info("Выводим один фильм...");
        if (id < 0) {
            log.warn("Ошибка при выводе фильма: Передан отрицательный ID");
            throw new EntityNotFoundException("Передан отрицательный ID");
        }
        return storage.getItem(id);
    }

    public void deleteHelper() {
        storage.deleteHelper();
    }


}
