package com.allterra.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller.
 */
@RestController
public final class TestController {

    /**
     * Test method.
     *
     * @return response
     */
    @GetMapping("/test")
    public @ResponseBody String greeting() {
        return "Hello, World";
    }
}
