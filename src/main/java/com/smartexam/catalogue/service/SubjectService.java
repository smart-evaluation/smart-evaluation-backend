package com.smartexam.catalogue.service;

import com.smartexam.catalogue.dto.request.CreateSubjectRequest;
import com.smartexam.catalogue.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(CreateSubjectRequest request);

    List<SubjectResponse> getSubjectsByExamLevel(Long examLevelId);

    SubjectResponse getSubjectById(Long id);
}