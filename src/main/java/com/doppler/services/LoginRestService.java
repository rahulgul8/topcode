package com.doppler.services;

import static com.doppler.util.ApiConstants.ADMIN_USER_KEY;
import static com.doppler.util.ApiConstants.BASE_URI;
import static com.doppler.util.ApiConstants.USER_KEY;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.doppler.entities.requests.LoginRestServiceRequest;
import com.doppler.entities.responses.LoginRestServiceResponse;

@Service
public class LoginRestService {

	private static final String lOGIN_SERVICE = "/users/login";
	private RestTemplate restTemplate = new RestTemplate();

	private LoginRestServiceRequest loginDetails = new LoginRestServiceRequest();

	public String login() {
		loginDetails.setKey(ADMIN_USER_KEY);
		return performLoginAndRetrieveToken(loginDetails);
	}

	public String userLogin() {
		loginDetails.setKey(USER_KEY);
		return performLoginAndRetrieveToken(loginDetails);
	}

	private String performLoginAndRetrieveToken(LoginRestServiceRequest loginRestServiceRequest) {
		ResponseEntity<LoginRestServiceResponse> response = this.restTemplate.postForEntity(BASE_URI + lOGIN_SERVICE,
				loginDetails, LoginRestServiceResponse.class);
		String token = response.getBody().getToken();
		return token;
	}
}
