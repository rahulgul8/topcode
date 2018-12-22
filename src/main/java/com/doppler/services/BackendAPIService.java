package com.doppler.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.doppler.security.SecurityUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
public class BackendAPIService extends AbstractRestTemplateHandler {

	@Autowired
	public BackendAPIService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		super(restTemplateBuilder, objectMapper);
	}

	protected HttpEntity<String> constructHttpEntityWithRequestHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", token);
		return new HttpEntity<>("parameters", headers);
	}

	protected <R> HttpEntity<R> constructHttpEntityWithRequestHeaders(String token, R body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", token);
		return new HttpEntity<>(body, headers);
	}

	private String getToken() {
		return SecurityUtils.getCurrentUser().getBackendToken();
	}

	public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
					constructHttpEntityWithRequestHeaders(getToken()), String.class, uriVariables);
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

	public <T> T getForEntity(Class<T> clazz, String url, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
					constructHttpEntityWithRequestHeaders(getToken()), String.class, uriVariables);
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

	public <T, R> T postForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
				constructHttpEntityWithRequestHeaders(getToken(), body), String.class, uriVariables);
		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
		return readValue(response, javaType);
	}

	public <T, R> T putForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT,
				constructHttpEntityWithRequestHeaders(getToken(), body), String.class, uriVariables);
		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
		return readValue(response, javaType);
	}

	public void delete(String url, Object... uriVariables) {
		try {
			restTemplate.exchange(url, HttpMethod.DELETE, constructHttpEntityWithRequestHeaders(getToken()),
					String.class, uriVariables);
		} catch (RestClientException exception) {
			LOGGER.info(exception.getMessage());
		}
	}
}
