package com.doppler.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The web application security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * The authentication entry point.
   */
  @Autowired
  private AuthenticationEntryPoint authenticationEntryPoint;

  /**
   * The access denied handler.
   */
  @Autowired
  private AccessDeniedHandler accessDeniedHandler;

  /**
   * Configure the JWT token filter bean.
   * 
   * @return the JWT authentication token filter
   */
  @Bean
  public BearerTokenFilter authenticationTokenFilter() throws Exception {
    return new BearerTokenFilter();
  }

  /**
   * Configure web MVC.
   * 
   * @return the web MVC configurer
   */
  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
      }
    };
  }

  /**
   * Configure the HTTP security.
   * 
   * @param httpSecurity the HTTP security
   * @throws Exception if any error occurs
   */
  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.cors();
    httpSecurity.csrf().disable();
    httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    httpSecurity.exceptionHandling() //
        .authenticationEntryPoint(authenticationEntryPoint) //
        .accessDeniedHandler(accessDeniedHandler);

    // Authorization
    httpSecurity.authorizeRequests()

        // Login
        .antMatchers("/login").anonymous() //

        // Scan ticket
        .antMatchers("/events/*/scanTicket").hasRole("M2M")

        // The others need authentication as user
        .anyRequest().hasRole("USER");

    // Custom bearer based security filter
    httpSecurity.addFilterBefore(authenticationTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);
  }
}
