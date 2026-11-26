package com.smartexam.catalogue.service.impl;

import com.smartexam.catalogue.dto.request.CreateTopicRequest;
import com.smartexam.catalogue.dto.response.TopicResponse;
import com.smartexam.catalogue.entity.Topic;
import com.smartexam.catalogue.exception.*;
import com.smartexam.catalogue.repository.*;
import com.smartexam.catalogue.service.TopicService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public TopicServiceImpl(
            TopicRepository topicRepository,
            SubjectRepository subjectRepository) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public TopicResponse createTopic(CreateTopicRequest request) {

        var subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found: " + request.subjectId()));

        var level = subject.getExamLevel();

        if (!Boolean.TRUE.equals(subject.getActive())
                || !Boolean.TRUE.equals(level.getActive())
                || !Boolean.TRUE.equals(level.getExam().getActive())
                || !Boolean.TRUE.equals(level.getExam()
                .getCategory().getActive())) {
            throw new IllegalArgumentException(
                    "Cannot add topic under an inactive catalogue");
        }

        String code = request.code().trim().toUpperCase();

        if (topicRepository.existsBySubjectIdAndCode(
                request.subjectId(), code)) {
            throw new DuplicateResourceException(
                    "Topic code already exists: " + code);
        }

        Topic topic = Topic.builder()
                .subject(subject)
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .displayOrder(request.displayOrder() == null
                        ? 0 : request.displayOrder())
                .active(true)
                .build();

        return map(topicRepository.save(topic));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsBySubject(Long subjectId) {

        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException(
                    "Subject not found: " + subjectId);
        }

        return topicRepository
                .findBySubjectIdAndActiveTrueOrderByDisplayOrderAsc(
                        subjectId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponse getTopicById(Long id) {
        return map(topicRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found: " + id)));
    }

    private TopicResponse map(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSubject().getId(),
                topic.getSubject().getName(),
                topic.getName(),
                topic.getCode(),
                topic.getDescription(),
                topic.getActive(),
                topic.getDisplayOrder(),
                topic.getCreatedAt()
        );
    }
}