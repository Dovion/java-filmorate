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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private RestTemplate restTemplate;

    private URI uri;

    private HttpHeaders headers;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    @LocalServerPort
    private int port;
    @Autowired
    private UserController userController;

    @BeforeEach
    void initRequest() throws URISyntaxException {
        this.restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + port + "/users/";
        this.uri = new URI(baseUrl);
        this.headers = new HttpHeaders();
        headers.set("Accept", "application/json");
    }

    @AfterEach
    void clear() {
        userController.deleteHelper();
    }


    @Test
    void shouldReturnTrueIfControllerExists() {
        assertThat(userController).isNotNull();
    }

    @Test
    void getRequest_shouldReturnCode200() {
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void getRequest_shouldReturnEmptyResponseBody_whenNoAnyUsers() {
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals("[]", result.getBody());
    }

    @Test
    void shouldReturnTrueIfStatusCodeIsOkAfterPostRequest() {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void postRequest_shouldReturnResponseBody_afterUpdateUserFields() {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(uri, request, String.class);
        User updatedUser = new User(1, "ex@example.ru", "logo", "newName", LocalDate.of(2021, 12, 12));
        restTemplate.put(String.valueOf(uri), updatedUser, User.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(List.of(gson.toJson(updatedUser)).toString(), result.getBody());
    }

    @Test
    void putRequest_shouldReturnResponseBodyWithCode400_whenIncorrectEmailPassed() {
        try {
            User user = new User(1, "Невалидный@эмейл.ру", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            restTemplate.postForEntity(uri, request, String.class);
            User updatedUser = new User(1, "exexample.ru", "logo", "newName", LocalDate.of(2021, 12, 12));
            restTemplate.put(String.valueOf(uri), updatedUser, User.class);
        } catch (Exception e) {
            Assertions.assertEquals("400", e.getMessage().split(" ")[0]);
        }

    }

    @Test
    void postRequest_shouldReturnCode400_whenIncorrectEmailPassed() {
        try {
            User user = new User(1, "Невалидный@эмейл.ру", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            restTemplate.postForEntity(uri, request, String.class);
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
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertEquals(gson.toJson(user), result.getBody());
    }

    @Test
    void getRequest_shouldReturnResponseBody_whenObjectFieldsPassed() {
        User user = new User(1, "example@example.com", "testLogin", "Vasya", LocalDate.of(2020, 12, 12));
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(uri, request, String.class);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        Assertions.assertEquals(List.of(gson.toJson(user)).toString(), result.getBody());
    }

}

