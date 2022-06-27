package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    @LocalServerPort
    private int port;
    @Autowired
    private UserController userController;
    @Autowired
    private TestRestTemplate restTemplate;
    @AfterEach
    void clear () {
        userController.deleteHelper();
    }


    @Test
    void shouldReturnTrueIfControllerExists() {
        assertThat(userController).isNotNull();
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterGetRequest() throws URISyntaxException {
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    void shouldReturnEmptyResponseBodyAfterGetRequestWithoutPostRequestsBefore() throws URISyntaxException { //TODO
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals("{}", result.getBody());
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterPostRequest() throws URISyntaxException {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void shouldReturnTrueIfResponseBodyEqualsRequestBodyAfterPutRequest() throws URISyntaxException {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(uri, request, String.class);
        User updatedUser = new User(1, "ex@example.ru", "logo", "newName", LocalDate.of(2021, 12, 12));
        restTemplate.put(String.valueOf(uri), updatedUser, User.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(Map.of(updatedUser.getId(), updatedUser).toString(), result.getBody());
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsBadRequestAfterPutRequestWithIncorrectRaw() throws URISyntaxException {
        try {
            User user = new User(1, "Невалидный@эмейл.ру", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
//            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/users/";
            URI uri = new URI(baseUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            restTemplate.postForEntity(uri, request, String.class);
            User updatedUser = new User(1, "exexample.ru", "logo", "newName", LocalDate.of(2021, 12, 12));
            restTemplate.put(String.valueOf(uri), updatedUser, User.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void shouldReturnTrueIfStatusCodeIsBadRequestAfterPostRequestWithIncorrectRaw() throws URISyntaxException {
        try {
            User user = new User(1, "Невалидный@эмейл.ру", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
//            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/users/";
            URI uri = new URI(baseUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            restTemplate.postForEntity(uri, request, String.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void shouldReturnTrueIfStatusCodeIsUnsupportedMediaTypeAfterPostRequestWithNullObject() {
        try {
//            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:" + port + "/users/";
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
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(gson.toJson(user), result.getBody());
    }

    @Test
    void shouldReturnTrueIfResponseBodyEqualsRequestBodyAfterGetRequestWithPostRequestBefore() throws URISyntaxException {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        String baseUrl = "http://localhost:" + port + "/users/";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(uri, request, String.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(Map.of(user.getId(), user).toString(), result.getBody());
    }

}

