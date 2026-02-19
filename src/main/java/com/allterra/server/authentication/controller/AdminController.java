package com.allterra.server.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin API.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * Returns admin endpoint status.
     *
     * @return status payload
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "ok", "scope", "admin"));
    }
}
