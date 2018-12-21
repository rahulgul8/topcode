package com.doppler.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.doppler.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Service
@Qualifier("backend")
public class BackendAPIService extends RestTemplateService {

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

	private String getToken() {
		return SecurityUtils.getCurrentUser().getBackendToken();
	}

	@Override
	public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
				constructHttpEntityWithRequestHeaders(getToken()), String.class, uriVariables);
		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
		return readValue(response, collectionType);
	}
}
