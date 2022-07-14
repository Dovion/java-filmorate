package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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
    public Film create(Film film) {
        if (film.getId() == null && films.isEmpty()) {
            film.setId(1);
        }
        if (film.getId() == null) {
            List<Integer> filmIDs = new ArrayList<>(films.keySet());
            film.setId(Collections.max(filmIDs) + 1);
        }
        log.info("Фильм успешно добавлен");
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film film) {
        log.info("Фильм успешно обновлён");
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film getItem(int id) {
        log.info("Вывод фильма произошёл успешно");
        return films.get(id);
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    public void deleteHelper() {
        films.clear();
    }

}
