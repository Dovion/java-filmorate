package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    private RestTemplate restTemplate;

    private URI uri;

    private HttpHeaders headers;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    @LocalServerPort
    private int port;
    @Autowired
    private FilmController filmController;

    @BeforeEach
    void clear() {
        filmController.deleteHelper();
    }

    @BeforeEach
    void initRequest() throws URISyntaxException {
        this.restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        this.uri = new URI(baseUrl);
        this.headers = new HttpHeaders();
        headers.set("Accept", "application/json");
    }

    @Test
    void shouldReturnTrueIfControllerExists() {
        assertThat(filmController).isNotNull();
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterGetRequest() {
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void getRequest_shouldReturnEmptyResponseBody_whenNoAnyFilms() {
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals("[]", result.getBody());
    }

    @Test
    void postRequest_shouldReturnCode200_whenCorrectDataPassed() {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void putRequest_shouldReturnChangedResponseBody_afterDataUpdate() {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        restTemplate.postForEntity(uri, request, String.class);
        Film updatedFilm = new Film(1, "ТестФильмUpd", "ТестДескUpd", LocalDate.of(2010, 12, 12), 100);
        restTemplate.put(String.valueOf(uri), updatedFilm, Film.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(List.of(gson.toJson(updatedFilm)).toString(), result.getBody());
    }

    @Test
    void putRequest_shouldReturnResponseBodyWithCode400_whenIncorrectDatePassed() {
        try {
            Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
            HttpEntity<Film> request = new HttpEntity<>(film, headers);
            restTemplate.postForEntity(uri, request, String.class);
            Film updatedFilm = new Film(1, "ТестФильмUpd", "ТестДескUpd", LocalDate.of(1777, 12, 12), 100);
            restTemplate.put(String.valueOf(uri), updatedFilm, Film.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void postRequest_shouldReturnCode415_whenRequestIsNull() {
        try {
            restTemplate.postForEntity(uri, null, String.class);
        } catch (Exception e) {
            Assertions.assertEquals("415", e.getMessage().split(" ")[0]);
        }
    }

    @Test
    void postRequest_shouldReturnResponseBody_whenObjectFieldsPassed() {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(gson.toJson(film), result.getBody());
    }

    @Test
    void getRequest_shouldReturnResponseBody_whenObjectFieldsPassed() {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        restTemplate.postForEntity(uri, request, String.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(List.of(gson.toJson(film)).toString(), result.getBody());
    }

}

