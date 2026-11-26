package com.smartexam.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/super-admin")
public class SuperAdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, String> getDashboard() {

        return Map.of(
                "message",
                "Welcome to SmartExam Super Admin Dashboard"
        );
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, String> createAdmin() {

        return Map.of(
                "message",
                "Admin creation endpoint authorized"
        );
    }
}