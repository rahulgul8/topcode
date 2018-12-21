package com.doppler.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit4.SpringRunner;
import com.doppler.entities.IdentifiableEntity;

/**
 * Base class for all repository test classes.
 * 
 * @param <T> the entity type
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseRepositoryTest<T extends IdentifiableEntity> {

  /**
   * The repository.
   */
  @Autowired
  protected BaseRepository<T> repository;

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
   * Run before each test.
   * 
   * @throws Exception if any error occurs
   */
  @Before
  public void before() throws Exception {
    if (connection == null) {
      connection = dataSource.getConnection();
    }

    insertTestData();
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
