package com.doppler.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import com.doppler.entities.UserBadge;

/**
 * The tests for UserBadgeRepository.
 */
public class UserBadgeRepositoryTest extends BaseRepositoryTest<UserBadge> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    UserBadge entity = new UserBadge();
    entity.setBadgeId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity = repository.saveAndFlush(entity);

    UserBadge dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setBadgeId(UUID.fromString("00000000-0000-0000-0003-000000000002"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);
  }

  /**
   * Negative test for save() with null topicId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullBadgeId() {
    UserBadge entity = new UserBadge();
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    UserBadge entity = new UserBadge();
    entity.setBadgeId(UUID.fromString("00000000-0000-0000-0003-000000000001"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non-existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNotExistedUserId() {
    UserBadge entity = new UserBadge();
    entity.setBadgeId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UserBadge entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUserId());
    assertEquals(UUID.fromString("00000000-0000-0000-0003-000000000002"), entity.getBadgeId());
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
   * Positive test for findByUserId().
   */
  @Test
  public void findByUserId() {
    // Found
    List<UserBadge> entities = ((UserBadgeRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    assertEquals(4, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals(UUID.fromString("00000000-0000-0000-0003-000000000001"),
        entities.get(0).getBadgeId());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"),
        entities.get(0).getUserId());

    // Not found
    entities = ((UserBadgeRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    assertEquals(0, entities.size());
  }
}
