package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    @LocalServerPort
    private int port;
    @Autowired()
    private FilmController filmController;
    @BeforeEach
    void clear() {
        filmController.deleteHelper();
    }

    @Test
    void shouldReturnTrueIfControllerExists() {
        assertThat(filmController).isNotNull();
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterGetRequest() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void shouldReturnEmptyResponseBodyAfterGetRequestWithoutPostRequestsBefore() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals("{}", result.getBody());
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterPostRequest() throws URISyntaxException {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void shouldReturnTrueIfResponseBodyEqualsRequestBodyAfterPutRequest() throws URISyntaxException {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        restTemplate.postForEntity(uri, request, String.class);
        Film updatedFilm = new Film(1, "ТестФильмUpd", "ТестДескUpd", LocalDate.of(2010, 12, 12), 100);
        restTemplate.put(String.valueOf(uri), updatedFilm, Film.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(Map.of(updatedFilm.getId(), updatedFilm).toString(), result.getBody());
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsBadRequestAfterPutRequestWithIncorrectRaw() throws URISyntaxException {
        try {
            Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/films/";
            URI uri = new URI(baseUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<Film> request = new HttpEntity<>(film, headers);
            restTemplate.postForEntity(uri, request, String.class);
            Film updatedFilm = new Film(1, "ТестФильмUpd", "ТестДескUpd", LocalDate.of(1777, 12, 12), 100);
            restTemplate.put(String.valueOf(uri), updatedFilm, Film.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void shouldReturnTrueIfStatusCodeIsBadRequestAfterPostRequestWithIncorrectRaw() throws URISyntaxException {
        try {
            Film film = new Film(null, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/films/";
            URI uri = new URI(baseUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<Film> request = new HttpEntity<>(film, headers);
            restTemplate.postForEntity(uri, request, String.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void shouldReturnTrueIfStatusCodeIsUnsupportedMediaTypeAfterPostRequestWithNullObject() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/films/";
            URI uri = new URI(baseUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            restTemplate.postForEntity(uri, null, String.class);
        } catch (Exception e) {
            Assertions.assertEquals("415", e.getMessage().split(" ")[0]);
        }
    }

    @Test
    void shouldReturnTrueIfResponseBodyEqualsRequestBodyAfterPostRequest() throws URISyntaxException {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(gson.toJson(film), result.getBody());
    }

    @Test
    void shouldReturnTrueIfResponseBodyEqualsRequestBodyAfterGetRequestWithPostRequestBefore() throws URISyntaxException {
        Film film = new Film(1, "ТестФильм", "ТестДеск", LocalDate.of(2000, 12, 12), 100);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/films/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        restTemplate.postForEntity(uri, request, String.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(Map.of(film.getId(), film).toString(), result.getBody());
    }

}

