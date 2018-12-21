package com.doppler.controllers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.doppler.entities.requests.LoginRequest;
import com.doppler.entities.responses.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for all controller test classes.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseControllerTest {

  /**
   * The web application context.
   */
  @Autowired
  private WebApplicationContext context;

  /**
   * The mock MVC.
   */
  protected static MockMvc mockMvc;

  /**
   * The object mapper.
   */
  @Autowired
  protected ObjectMapper objectMapper;

  /**
   * The datasource.
   */
  @Autowired
  private DataSource dataSource;

  /**
   * The db connection.
   */
  private static Connection connection;

  /**
   * The user bearer token.
   */
  protected String userBearerToken;

  /**
   * The user2 (already submitted survey/quiz) bearer token.
   */
  protected String user2BearerToken;

  /**
   * The user5 (isNew = true) bearer token.
   */
  protected String user5BearerToken;

  /**
   * Run before each test.
   * 
   * @throws Exception if any error occurs
   */
  @Before
  public void before() throws Exception {
    if (mockMvc == null) {
      mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    if (connection == null) {
      connection = dataSource.getConnection();
    }

    // Insert test data
    insertTestData();

    // Login and cache the token
    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user1@doppler.com", "password"))))
        .andDo(x -> {
          LoginResponse response =
              objectMapper.readValue(x.getResponse().getContentAsString(), LoginResponse.class);
          userBearerToken = "Bearer " + response.getAccessToken();
        });

    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user2@doppler.com", "password"))))
        .andDo(x -> {
          LoginResponse response =
              objectMapper.readValue(x.getResponse().getContentAsString(), LoginResponse.class);
          user2BearerToken = "Bearer " + response.getAccessToken();
        });

    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(
            objectMapper.writeValueAsString(new LoginRequest("user5@doppler.com", "password"))))
        .andDo(x -> {
          LoginResponse response =
              objectMapper.readValue(x.getResponse().getContentAsString(), LoginResponse.class);
          user5BearerToken = "Bearer " + response.getAccessToken();
        });
  }

  /**
   * Remove test data.
   * 
   * @throws Exception if any error occurs
   */
  @AfterClass
  public static void clearTestData() throws Exception {
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("clear-test-data.sql"));

    connection.close();
    connection = null;
  }

  /**
   * Insert test data.
   * 
   * @throws Exception if any error occurs
   */
  private static void insertTestData() throws ScriptException, SQLException {
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("insert-test-data.sql"));
  }
}
