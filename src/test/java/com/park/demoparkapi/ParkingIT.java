package com.park.demoparkapi;

import com.park.demoparkapi.web.dto.PageableDto;
import com.park.demoparkapi.web.dto.ParkingCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking/parking-create.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking/parking-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingIT {

    @Autowired
    private WebTestClient testClient;

    @Test
    public void createCheckin_withValidData_ReturningCreatedAndLocation() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("XVR2M67").carBrand("FIAT").carModel("PALIO 1.0")
                .carColor("BLUE").clientCpf("09191773016")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("XVR2M67")
                .jsonPath("carBrand").isEqualTo("FIAT")
                .jsonPath("carModel").isEqualTo("PALIO 1.0")
                .jsonPath("carColor").isEqualTo("BLUE")
                .jsonPath("clientCpf").isEqualTo("09191773016")
                .jsonPath("receipt").exists()
                .jsonPath("entryDate").exists()
                .jsonPath("carSpaceCode").exists();
    }

    @Test
    public void createCheckin_WithClientRole_ReturningErrorStatus403() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("XVR2M67").carBrand("FIAT").carModel("PALIO 1.0")
                .carColor("BLUE").clientCpf("09191773016")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckin_withInvalidData_ReturningErrorStatus422() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("").carBrand("").carModel("")
                .carColor("").clientCpf("")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckin_WithNonexistingCpf_ReturningErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("XVR2M67").carBrand("FIAT").carModel("PALIO 1.0")
                .carColor("BLUE").clientCpf("33838667000")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/parking/parking-create-occupied-carspace.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/parking/parking-delete-occupied-carspace.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckin_WithOccupiedCarSpace_ReturningErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("XVR2M67").carBrand("FIAT").carModel("PALIO 1.0")
                .carColor("AZUL").clientCpf("09191773016")
                .build();

        testClient.post().uri("/api/v1/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void findCheckin_withAdminRole_ReturningDataStatus200() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("license_plate").isEqualTo("UJB2S76")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("GREEN")
                .jsonPath("clientCpf").isEqualTo("98401203015")
                .jsonPath("receipt").isEqualTo("20230313-101300")
                .jsonPath("entry_date").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("id_carspace").isEqualTo("A-01");
    }

    @Test
    public void findCheckin_withClientRole_ReturningDataStatus200() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("license_plate").isEqualTo("UJB2S76")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("GREEN")
                .jsonPath("clientCpf").isEqualTo("98401203015")
                .jsonPath("receipt").isEqualTo("20230313-101300")
                .jsonPath("entry_date").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("id_carspace").isEqualTo("A-01");
    }

    @Test
    public void findCheckin_WithNonexistingReceipt_ReturningErrorStatus404() {

        testClient.get()
                .uri("/api/v1/parking/check-in/{receipt}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-in/20230313-999999")
                .jsonPath("method").isEqualTo("GET");

    }

    @Test
    public void createCheckOut_WithExistingReceipt_ReturningSuccess() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("license_plate").isEqualTo("UJB2S76")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("VERDE")
                .jsonPath("entry_date").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("clientCpf").isEqualTo("98401203015")
                .jsonPath("id_carspace").isEqualTo("A-01")
                .jsonPath("receipt").isEqualTo("20230313-101300")
                .jsonPath("endDate").exists()
                .jsonPath("value").exists()
                .jsonPath("discount").exists();
    }

    @Test
    public void  createCheckOut_WithNonexistingReceipt_ReturningErrorStatus404() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-out/20230313-000000")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void createCheckOut_WithClientRole_ReturningErrorStatus403() {

        testClient.put()
                .uri("/api/v1/parking/check-out/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking/check-out/20230313-101300")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void findParking_ByClientCpf_ReturningSucesso() {

        PageableDto responseBody = testClient.get()
                .uri("/api/v1/parking/cpf/{cpf}?size=1&page=0", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/parking/cpf/{cpf}?size=1&page=1", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void findParking_ByClientCpfWithClientRole_ReturningErrorStatus403() {

        testClient.get()
                .uri("/api/v1/parking/cpf/{cpf}", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking/cpf/98401203015")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void findParking_WithAuthenticatedClient_ReturningSuccess() {

        PageableDto responseBody = testClient.get()
                .uri("/api/v1/parking?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/parking?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void findParking_WithAdminRole_ReturningErrorStatus403() {

        testClient.get()
                .uri("/api/v1/parking")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking")
                .jsonPath("method").isEqualTo("GET");
    }
}
