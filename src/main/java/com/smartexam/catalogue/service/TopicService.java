package com.smartexam.catalogue.service;

import com.smartexam.catalogue.dto.request.CreateTopicRequest;
import com.smartexam.catalogue.dto.response.TopicResponse;

import java.util.List;

public interface TopicService {

    TopicResponse createTopic(CreateTopicRequest request);

    List<TopicResponse> getTopicsBySubject(Long subjectId);

    TopicResponse getTopicById(Long id);
}