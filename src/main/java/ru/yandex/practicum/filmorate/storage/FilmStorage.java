package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public List<Film> getAll();

    public Film create(Film film) throws ValidationException;

    public Film update(Film film) throws ValidationException, NotFoundException;

    public Film getItem(Integer id) throws NotFoundException;

    public Map<Integer, Film> getFilmsMap();

    public void deleteHelper();
}
