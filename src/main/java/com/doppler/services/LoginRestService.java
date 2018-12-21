package com.doppler.services;

import static com.doppler.util.ApiConstants.ADMIN_USER_KEY;
import static com.doppler.util.ApiConstants.BASE_URI;
import static com.doppler.util.ApiConstants.USER_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.doppler.entities.requests.LoginRestServiceRequest;
import com.doppler.entities.responses.LoginRestServiceResponse;

@Service
public class LoginRestService {

	public static final String LOGIN_SERVICE = "/users/login";

	@Autowired
	@Qualifier(value = "common")
	private RestTemplateService helper;

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
		LoginRestServiceResponse response = this.helper.postForEntity(LoginRestServiceResponse.class,
				BASE_URI + LOGIN_SERVICE, loginRestServiceRequest);
		if (response != null) {
			return response.getToken();
		}
		return null;
	}
}
