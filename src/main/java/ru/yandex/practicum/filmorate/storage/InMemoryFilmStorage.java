package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();


    @Override
    public List<Film> getAll() {
        List<Film> filmsList = new ArrayList<>();
        for (var film : films.values()) {
            filmsList.add(film);
        }
        return filmsList;
    }

    @Override
    public Film create(Film film) throws ValidationException {
        log.info("Добавляем фильм...");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при добавлении фильма: неверная дата релиза");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка при добавлении фильма: нулевая продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getId() == null && films.isEmpty()) {
            film.setId(1);
        }
        if (film.getId() == null) {
            List<Integer> userIDs = new ArrayList<>();
            for (Integer id : films.keySet()) {
                userIDs.add(id);
            }
            var minID = Collections.min(userIDs);
            if (minID > 1) {
                minID = 0;
            }
            for (Integer i : userIDs) {
                if (!userIDs.contains(minID + 1)) {
                    film.setId(minID + 1);
                    break;
                } else {
                    minID++;
                }
            }
        }
        log.info("Фильм успешно добавлен");
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        log.info("Обновляем фильм...");
        if (films.containsKey(film.getId())) {
            log.info("Фильм успешно обновлён");
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        log.warn("Ошибка при добавлении фильма: отсутствует ID");
        throw new NotFoundException("ID фильма отсутствует в базе данных");
    }

    @Override
    public Film getItem(Integer id) throws NotFoundException {
        log.info("Выводим один фильм...");
        if(id < 0){
            log.warn("Ошибка при выводе фильма: Передан отрицательный ID");
            throw new NotFoundException("Передан отрицательный ID");
        }
        try {
            log.info("Вывод фильма произошёл успешно");
            return films.get(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    public void deleteHelper() {
        films.clear();
    }

}