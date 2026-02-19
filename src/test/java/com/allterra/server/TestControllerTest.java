package com.allterra.server;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestControllerTest {

    @Test
    void greetingShouldReturnHelloWorld() {
        var controller = new TestController();

        assertThat(controller.greeting()).isEqualTo("Hello, World");
    }
}
