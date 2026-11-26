package com.smartexam.student.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public Map<String, String> getDashboard() {

        return Map.of(
                "message",
                "Welcome to SmartExam Student Dashboard"
        );
    }

    @GetMapping("/my-exams")
    @PreAuthorize("hasRole('STUDENT')")
    public Map<String, String> getMyExams() {

        return Map.of(
                "message",
                "Student exam listing endpoint authorized"
        );
    }
}