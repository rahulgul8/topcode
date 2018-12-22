package com.doppler.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractRestTemplateHandler {

	protected RestTemplate restTemplate;
	protected ObjectMapper objectMapper;

	protected static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);

	public AbstractRestTemplateHandler(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		this.restTemplate = restTemplateBuilder.build();
		this.objectMapper = objectMapper;
	}

	protected <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
		T result = null;
		if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED
				|| response.getStatusCode() == HttpStatus.ACCEPTED) {
			try {
				result = objectMapper.readValue(response.getBody(), javaType);
			} catch (IOException e) {
				LOGGER.info(e.getMessage());
			}
		} else {
			LOGGER.info("No data found {}", response.getStatusCode());
		}
		return result;
	}

}
