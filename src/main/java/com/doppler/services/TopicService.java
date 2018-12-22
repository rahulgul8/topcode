package com.doppler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doppler.entities.Topic;
import com.doppler.util.ApiConstants;

/**
 * The service provides topic related operations.
 */
@Service
@Transactional
public class TopicService extends BaseService {

	private static final String TOPIC_URL = "/topics";

	@Autowired
	BackendAPIService apiService;

	public List<Topic> retrieveTopics() {
		List<Topic> topics = apiService.getForList(Topic.class, ApiConstants.BASE_URI + TOPIC_URL);
		return topics;
	}
}
