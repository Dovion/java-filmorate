package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public List<Film> getAll();

    public Film create(Film film);

    public Film update(Film film);

    public Film getItem(int id);

    public Map<Integer, Film> getFilmsMap();

    public void deleteHelper();
}
