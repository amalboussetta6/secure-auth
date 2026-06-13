package com.example.secure_auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "This endpoint is public");
    }

    @GetMapping("/user")
    public Map<String, Object> userEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Hello authenticated user",
                "username", jwt.getClaimAsString("preferred_username"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
    }

    @GetMapping("/admin")
    public Map<String, Object> adminEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Hello admin",
                "username", jwt.getClaimAsString("preferred_username"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
    }
}