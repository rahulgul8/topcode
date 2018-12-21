package com.doppler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.doppler.entities.Topic;
import com.doppler.util.ApiConstants;

/**
 * The service provides topic related operations.
 */
@Service
@Transactional
public class TopicService extends BaseService {

	private static final String TOPIC_URL = "/topics";

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	LoginRestService loginService;

	public List<Topic> retrieveTopics() {
		// Logging into Application to obtain new session id
		String token = loginService.login();
		ResponseEntity<List> response = restTemplate.exchange(ApiConstants.BASE_URI + TOPIC_URL, HttpMethod.GET,
				constructHttpEntityWithRequestHeaders(token), List.class);

		return (List<Topic>) response.getBody();
	}
}
