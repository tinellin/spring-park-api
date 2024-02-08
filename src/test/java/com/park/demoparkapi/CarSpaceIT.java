package com.park.demoparkapi;

import com.park.demoparkapi.web.dto.CarSpaceCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/carspaces/carspaces-create.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/carspaces/carspaces-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CarSpaceIT {

    @Autowired
    private WebTestClient testClient;

    @Test
    public void createCarSpace_withValidData_ReturningLocationStatus201() {
        testClient
                .post()
                .uri("/api/v1/carspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new CarSpaceCreateDto("A-05", "FREE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void createCarSpace_WithExistingCode_ReturningErrorMessageWithStatus409() {
        testClient
                .post()
                .uri("/api/v1/carspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new CarSpaceCreateDto("A-01", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/carspaces");

    }

    @Test
    public void createCarSpace_WithUnauthorizedUser_ReturningErrorMessageWithStatus403() {
        testClient
                .post()
                .uri("/api/v1/carspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .bodyValue(new CarSpaceCreateDto("A-01", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/carspaces");

    }

    @Test
    public void createCarSpace_WithInvalidData_ReturningErrorMessageWithStatus409() {
        testClient
                .post()
                .uri("/api/v1/carspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new CarSpaceCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/carspaces");

        testClient
                .post()
                .uri("/api/v1/carspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new CarSpaceCreateDto("A-501", "OCCUPIED"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/carspaces");
    }

    @Test
    public void findCarSpace_WithExistingCode_ReturningCarSpaceWithStatus200() {
        testClient
                .get()
                .uri("/api/v1/carspaces/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(10)
                .jsonPath("code").isEqualTo("A-01")
                .jsonPath("status").isEqualTo("FREE");

    }

    @Test
    public void findCarSpace_WithNonexistingCode_ReturningErrorMessageWithStatus404() {
        testClient
                .get()
                .uri("/api/v1/carspaces/{code}", "A-10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/carspaces/A-10");
    }

    @Test
    public void findCarSpace_WithUnauthorizedUser_ReturningErrorMessageWithStatus403() {
        testClient
                .get()
                .uri("/api/v1/carspaces/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/carspaces/A-01");
    }
}
