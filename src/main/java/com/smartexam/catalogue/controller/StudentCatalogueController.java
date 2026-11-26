
package com.smartexam.catalogue.controller;

import com.smartexam.catalogue.dto.response.*;
import com.smartexam.catalogue.service.*;
import com.smartexam.common.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogue")
@PreAuthorize("hasRole('STUDENT')")
public class StudentCatalogueController {

    private final ExamCategoryService categoryService;
    private final ExamService examService;
    private final ExamLevelService levelService;
    private final SubjectService subjectService;
    private final TopicService topicService;

    public StudentCatalogueController(
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

    // 1. Get active exam categories

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ExamCategoryResponse>>>
    getCategories() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam categories fetched successfully",
                        categoryService.getActiveCategories()
                )
        );
    }

    // 2. Get active exams by category

    @GetMapping("/categories/{categoryId}/exams")
    public ResponseEntity<ApiResponse<List<ExamResponse>>>
    getExams(@PathVariable Long categoryId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exams fetched successfully",
                        examService.getExamsByCategory(categoryId)
                )
        );
    }

    // 3. Get active levels by exam

    @GetMapping("/exams/{examId}/levels")
    public ResponseEntity<ApiResponse<List<ExamLevelResponse>>>
    getLevels(@PathVariable Long examId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exam levels fetched successfully",
                        levelService.getLevelsByExam(examId)
                )
        );
    }

    // 4. Get active subjects by level

    @GetMapping("/levels/{levelId}/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>>
    getSubjects(@PathVariable Long levelId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subjects fetched successfully",
                        subjectService.getSubjectsByExamLevel(levelId)
                )
        );
    }

    // 5. Get active topics by subject

    @GetMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<ApiResponse<List<TopicResponse>>>
    getTopics(@PathVariable Long subjectId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Topics fetched successfully",
                        topicService.getTopicsBySubject(subjectId)
                )
        );
    }
}
