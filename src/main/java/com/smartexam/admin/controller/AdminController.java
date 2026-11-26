package com.smartexam.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Map<String, String> getDashboard() {

        return Map.of(
                "message",
                "Welcome to SmartExam Admin Dashboard"
        );
    }

    @PostMapping("/create-exam")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Map<String, String> createExam() {

        return Map.of(
                "message",
                "Exam creation endpoint authorized"
        );
    }
}