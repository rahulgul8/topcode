package com.doppler.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
public class RestTemplateService extends AbstractRestTemplateHandler{


	@Autowired
	public RestTemplateService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		super(restTemplateBuilder, objectMapper);
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

	

}
