package com.doppler.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.EventCommentLike;

/**
 * The tests for EventCommentLikeRepository.
 */
public class EventCommentLikeRepositoryTest extends BaseRepositoryTest<EventCommentLike> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    EventCommentLike entity = new EventCommentLike();
    entity.setEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    entity = repository.saveAndFlush(entity);

    EventCommentLike dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);
  }

  /**
   * Negative test for save() with null eventCommentId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullEventCommentId() {
    EventCommentLike entity = new EventCommentLike();
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non existed eventCommentId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNonExistedEventCommentId() {
    EventCommentLike entity = new EventCommentLike();
    entity.setEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    EventCommentLike entity = new EventCommentLike();
    entity.setEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNonExistedUserId() {
    EventCommentLike entity = new EventCommentLike();
    entity.setEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    EventCommentLike entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"),
        entity.getEventCommentId());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), entity.getUserId());
    assertEquals("2018-10-02", String.format("%tY-%<tm-%<td", entity.getCreatedAt()));
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
   * Positive test for countByEventCommentId().
   */
  @Test
  public void countByEventCommentId() {
    // Found
    long count = ((EventCommentLikeRepository) repository)
        .countByEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    assertEquals(3, count);

    // Not found
    count = ((EventCommentLikeRepository) repository)
        .countByEventCommentId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    assertEquals(0, count);
  }

  /**
   * Positive test for existsByEventCommentIdAndUserId().
   */
  @Test
  public void existsByEventCommentIdAndUserId() {
    // Found
    boolean existed = ((EventCommentLikeRepository) repository).existsByEventCommentIdAndUserId(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000003"));

    assertEquals(true, existed);

    // Not found
    existed = ((EventCommentLikeRepository) repository).existsByEventCommentIdAndUserId(
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        UUID.fromString("00000000-0000-0000-0000-000000000002"));
    assertEquals(false, existed);
  }

  /**
   * Positive test for deleteByEventCommentIdAndUserId().
   */
  @Test
  @Transactional
  public void deleteByEventCommentIdAndUserId() {
    ((EventCommentLikeRepository) repository).deleteByEventCommentIdAndUserId(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000003"));

    boolean existed = ((EventCommentLikeRepository) repository).existsByEventCommentIdAndUserId(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000003"));

    assertEquals(false, existed);
  }
}
