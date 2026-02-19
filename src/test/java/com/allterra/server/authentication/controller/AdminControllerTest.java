package com.allterra.server.authentication.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdminControllerTest {

    @Test
    void statusShouldReturnOkPayload() {
        var controller = new AdminController();

        var response = controller.status();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("status", "ok");
        assertThat(response.getBody()).containsEntry("scope", "admin");
    }
}
