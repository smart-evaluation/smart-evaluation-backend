
package com.smartexam.catalogue.controller;

import com.smartexam.common.response.ApiResponse;
import com.smartexam.catalogue.dto.request.*;
import com.smartexam.catalogue.dto.response.*;
import com.smartexam.catalogue.service.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/catalogue")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminCatalogueController {

    private final ExamCategoryService categoryService;
    private final ExamService examService;
    private final ExamLevelService levelService;
    private final SubjectService subjectService;
    private final TopicService topicService;

    public AdminCatalogueController(
            ExamCategoryService categoryService,
            ExamService examService,
            ExamLevelService levelService,
            SubjectService subjectService,
            TopicService topicService) {

        this.categoryService = categoryService;
        this.examService = examService;
        this.levelService = levelService;
        this.subjectService = subjectService;
        this.topicService = topicService;
    }

    // ==========================================
    // EXAM CATEGORY APIs
    // ==========================================

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ExamCategoryResponse>>
    createCategory(
            @Valid @RequestBody CreateExamCategoryRequest request) {

        ExamCategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Exam category created successfully",
                        response));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ExamCategoryResponse>>>
    getAllCategories() {

        return ResponseEntity.ok(ApiResponse.success(
                "Exam categories fetched successfully",
                categoryService.getAllCategories()));
    }

    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<List<ExamCategoryResponse>>>
    getActiveCategories() {

        return ResponseEntity.ok(ApiResponse.success(
                "Active exam categories fetched successfully",
                categoryService.getActiveCategories()));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<ExamCategoryResponse>>
    getCategoryById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Exam category fetched successfully",
                categoryService.getCategoryById(id)));
    }

    // ==========================================
    // EXAM APIs
    // ==========================================

    @PostMapping("/exams")
    public ResponseEntity<ApiResponse<ExamResponse>>
    createExam(
            @Valid @RequestBody CreateExamRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Exam created successfully",
                        examService.createExam(request)));
    }

    @GetMapping("/categories/{categoryId}/exams")
    public ResponseEntity<ApiResponse<List<ExamResponse>>>
    getExamsByCategory(@PathVariable Long categoryId) {

        return ResponseEntity.ok(ApiResponse.success(
                "Exams fetched successfully",
                examService.getExamsByCategory(categoryId)));
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>>
    getExamById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Exam fetched successfully",
                examService.getExamById(id)));
    }

    // ==========================================
    // EXAM LEVEL APIs
    // ==========================================

    @PostMapping("/levels")
    public ResponseEntity<ApiResponse<ExamLevelResponse>>
    createExamLevel(
            @Valid @RequestBody CreateExamLevelRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Exam level created successfully",
                        levelService.createExamLevel(request)));
    }

    @GetMapping("/exams/{examId}/levels")
    public ResponseEntity<ApiResponse<List<ExamLevelResponse>>>
    getLevelsByExam(@PathVariable Long examId) {

        return ResponseEntity.ok(ApiResponse.success(
                "Exam levels fetched successfully",
                levelService.getLevelsByExam(examId)));
    }

    // ==========================================
    // SUBJECT APIs
    // ==========================================

    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<SubjectResponse>>
    createSubject(
            @Valid @RequestBody CreateSubjectRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Subject created successfully",
                        subjectService.createSubject(request)));
    }

    @GetMapping("/levels/{levelId}/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>>
    getSubjectsByLevel(@PathVariable Long levelId) {

        return ResponseEntity.ok(ApiResponse.success(
                "Subjects fetched successfully",
                subjectService.getSubjectsByExamLevel(levelId)));
    }

    // ==========================================
    // TOPIC APIs
    // ==========================================

    @PostMapping("/topics")
    public ResponseEntity<ApiResponse<TopicResponse>>
    createTopic(
            @Valid @RequestBody CreateTopicRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Topic created successfully",
                        topicService.createTopic(request)));
    }

    @GetMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<ApiResponse<List<TopicResponse>>>
    getTopicsBySubject(@PathVariable Long subjectId) {

        return ResponseEntity.ok(ApiResponse.success(
                "Topics fetched successfully",
                topicService.getTopicsBySubject(subjectId)));
    }
}
