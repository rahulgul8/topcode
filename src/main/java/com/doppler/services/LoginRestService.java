package com.doppler.services;

import static com.doppler.util.ApiConstants.ADMIN_USER_KEY;
import static com.doppler.util.ApiConstants.BASE_URI;
import static com.doppler.util.ApiConstants.USER_KEY;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.doppler.entities.User;
import com.doppler.entities.requests.LoginRestServiceRequest;
import com.doppler.entities.responses.LoginRestServiceResponse;
import com.doppler.repositories.UserRepository;
import com.doppler.security.BearerTokenFilter;
import com.doppler.security.SecurityUtils;

@Service
public class LoginRestService {

	public static final String LOGIN_SERVICE = "/users/login";

	@Autowired
	private RestTemplateService helper;

	@Autowired
	private HttpServletRequest request;

	/**
	 * The user repository.
	 */
	@Autowired
	private UserRepository userRepository;

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

	public void reauthenticateBackendServer() {
		// Re authenticate to the backend server
		String token = userLogin();

		// Update the retrieved token in the database.
		User user = userRepository.findByEmailIgnoreCase(SecurityUtils.getCurrentUser().getEmail());
		user.setBackendToken(token);
		userRepository.save(user);

		// Update the user token on the Security context
		UsernamePasswordAuthenticationToken authentication = BearerTokenFilter
				.getUsernamePasswordAuthenticationToken(user);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
