package com.doppler.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import com.doppler.entities.User;

/**
 * The tests for UserRepository.
 */
public class UserRepositoryTest extends BaseRepositoryTest<User> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    User user = new User();
    user.setDivision("division 1");
    user.setFullName("full name 1");
    user.setLocation("location 1");
    user.setPhoneNumber("0123456");
    user.setPosition("position 1");
    user.setEmail("user.new@doppler.com");

    user = repository.saveAndFlush(user);

    User dbUser = repository.findById(user.getId()).get();
    assertEquals(user, dbUser);

    // Update
    user.setEmail("user.updated@doppler.com");
    user.setDivision("division 1 new");
    user.setFullName("full name 1 new");
    user.setLocation("location 1 new");
    user.setPhoneNumber("0123456 new");
    user.setPosition("position 1 new");
    repository.saveAndFlush(user);

    dbUser = repository.findById(user.getId()).get();
    assertEquals(user, dbUser);
  }

  /**
   * Negative test for save() with null email.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullEmail() {
    User user = new User();

    repository.saveAndFlush(user);
  }

  /**
   * Negative test for save() with duplicate email.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createDuplicateEmail() {
    User user = new User();
    user.setEmail("user1@doppler.com");

    repository.saveAndFlush(user);
  }

  /**
   * Negative test for save() an existing user with duplicate email.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_updateDuplicateEmail() {
    User user = new User();
    user.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    user.setEmail("user2@doppler.com");

    repository.saveAndFlush(user);
  }

  /**
   * Negative test for save() with null phone number.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullPhoneNumber() {
    User user = new User();
    user.setEmail("user9@doppler.com");

    repository.saveAndFlush(user);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    User user = repository.findById(id).get();

    assertEquals(id, user.getId());
    assertEquals("user1@doppler.com", user.getEmail());
  }

  /**
   * Negative test for findById().
   */
  @Test(expected = NoSuchElementException.class)
  public void findById_notFound() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000009");
    repository.findById(id).get();
  }

  /**
   * Positive test for deleteById().
   */
  @Test
  public void deleteById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    repository.deleteById(id);

    assertFalse(repository.existsById(id));
  }

  /**
   * Negative test for deleteById().
   */
  @Test(expected = EmptyResultDataAccessException.class)
  public void deleteById_notFound() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000009");
    repository.deleteById(id);
  }

  /**
   * Positive test for findByEmailIgnoreCase().
   */
  @Test
  public void findByEmailIgnoreCase() {
    // Found
    User user = ((UserRepository) repository).findByEmailIgnoreCase("user1@doppler.com");

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), user.getId());
    assertEquals("user1@doppler.com", user.getEmail());

    // Not found
    user = ((UserRepository) repository).findByEmailIgnoreCase("user9@doppler.com");

    assertNull(user);
  }

  /**
   * Positive test for findByFullNameContainingIgnoreCase().
   */
  @Test
  public void findByFullNameContainingIgnoreCase() {
    // Found
    List<User> users = ((UserRepository) repository).findByFullNameContainingIgnoreCase("user");

    assertEquals(5, users.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), users.get(0).getId());

    users = ((UserRepository) repository).findByFullNameContainingIgnoreCase("1");
    assertEquals(1, users.size());

    // Not found
    users = ((UserRepository) repository).findByFullNameContainingIgnoreCase("notfound");
    assertEquals(0, users.size());
  }
}
