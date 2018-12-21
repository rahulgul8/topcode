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
import com.doppler.entities.UserRewardPoint;

/**
 * The tests for UserRewardRepository.
 */
public class UserRewardPointRepositoryTest extends BaseRepositoryTest<UserRewardPoint> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("description");
    entity.setPoints(10);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity = repository.saveAndFlush(entity);

    UserRewardPoint dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setDescription("updated description");
    entity.setPoints(100);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);
  }

  /**
   * Negative test for save() with null description.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullDescription() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setPoints(10);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with empty description.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createEmptyDescription() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("");
    entity.setPoints(10);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null points.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullPoints() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("description");
    entity.setPoints(null);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with zero points.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createZeroPoints() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("description");
    entity.setPoints(0);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("description");
    entity.setPoints(10);

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non-existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNotExistedUserId() {
    UserRewardPoint entity = new UserRewardPoint();
    entity.setDescription("description");
    entity.setPoints(10);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UserRewardPoint entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUserId());
    assertEquals(new Integer(2), entity.getPoints());
    assertEquals("reward 2", entity.getDescription());
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
    List<UserRewardPoint> entities = ((UserRewardPointRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    assertEquals(4, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals(new Integer(1), entities.get(0).getPoints());
    assertEquals("reward 1", entities.get(0).getDescription());

    // Not found
    entities = ((UserRewardPointRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    assertEquals(0, entities.size());
  }
}
