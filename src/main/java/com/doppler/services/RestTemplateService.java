package com.doppler.services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
@Qualifier("common")
public class RestTemplateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);

	protected RestTemplate restTemplate;
	protected ObjectMapper objectMapper;

	@Autowired
	public RestTemplateService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		this.restTemplate = restTemplateBuilder.build();
		this.objectMapper = objectMapper;
	}

	public <T> T getForEntity(Class<T> clazz, String url, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
			JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
			return readValue(response, javaType);
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.info("No data found {}", url);
			} else {
				LOGGER.info("rest client exception", exception.getMessage());
			}
		}
		return null;
	}

	public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
			CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
			return readValue(response, collectionType);
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.info("No data found {}", url);
			} else {
				LOGGER.info("rest client exception", exception.getMessage());
			}
		}
		return Collections.emptyList();
	}

	public <T, R> T postForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
		HttpEntity<R> request = new HttpEntity<>(body);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, uriVariables);
		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
		return readValue(response, javaType);
	}

	public <T, R> T putForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
		HttpEntity<R> request = new HttpEntity<>(body);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class,
				uriVariables);
		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
		return readValue(response, javaType);
	}

	public void delete(String url, Object... uriVariables) {
		try {
			restTemplate.delete(url, uriVariables);
		} catch (RestClientException exception) {
			LOGGER.info(exception.getMessage());
		}
	}

	protected <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
		T result = null;
		if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.ACCEPTED) {
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
