package com.doppler.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import com.doppler.entities.EventComment;

/**
 * The tests for EventCommentRepository.
 */
public class EventCommentRepositoryTest extends BaseRepositoryTest<EventComment> {

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    EventComment entity = new EventComment();
    entity.setContent("content");
    entity.setEventId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setParentCommentId(null);
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000003")));
    entity = repository.saveAndFlush(entity);

    EventComment dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity.getEventId(), dbEntity.getEventId());
    assertEquals(entity.getUser().getId(), dbEntity.getUser().getId());
    assertEquals(entity.getContent(), dbEntity.getContent());
    assertEquals(entity.getParentCommentId(), dbEntity.getParentCommentId());

    // Update
    entity.setContent("new content");
    entity.setParentCommentId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity.getEventId(), dbEntity.getEventId());
    assertEquals(entity.getUser().getId(), dbEntity.getUser().getId());
    assertEquals(entity.getContent(), dbEntity.getContent());
    assertEquals(entity.getParentCommentId(), dbEntity.getParentCommentId());
  }

  /**
   * Negative test for save() with null eventId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullEventId() {
    EventComment entity = new EventComment();
    entity.setContent("content");
    entity.setParentCommentId(null);
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000003")));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null user.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUser() {
    EventComment entity = new EventComment();
    entity.setContent("content");
    entity.setEventId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setParentCommentId(null);

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null content.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullContent() {
    EventComment entity = new EventComment();
    entity.setContent(null);
    entity.setEventId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setParentCommentId(null);
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000003")));


    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with empty content.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createEmptyContent() {
    EventComment entity = new EventComment();
    entity.setContent("");
    entity.setEventId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setParentCommentId(null);
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000003")));


    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non existed parentCommentId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNonExistedParentCommentId() {
    EventComment entity = new EventComment();
    entity.setContent(null);
    entity.setEventId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
    entity.setParentCommentId(UUID.fromString("00000000-0000-0000-0003-000000000009"));
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000003")));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    EventComment entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUser().getId());
    assertEquals(UUID.fromString("00000000-0000-0000-0002-000000000001"), entity.getEventId());
    assertEquals("content 2", entity.getContent());
    assertEquals(null, entity.getParentCommentId());
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
   * Positive test for findByEventId().
   */
  @Test
  public void findByEventId() {
    // Found
    Page<EventComment> pagedEntities = ((EventCommentRepository) repository)
        .findByEventId(UUID.fromString("00000000-0000-0000-0002-000000000001"), null);
    List<EventComment> entities = pagedEntities.getContent();

    assertEquals(4, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals(UUID.fromString("00000000-0000-0000-0002-000000000001"),
        entities.get(0).getEventId());
    assertEquals("content 1", entities.get(0).getContent());
    assertEquals(null, entities.get(0).getParentCommentId());
    assertEquals("2018-10-01", String.format("%tY-%<tm-%<td", entities.get(0).getCreatedAt()));

    // Sort by createdAt desc
    Pageable paging = PageRequest.of(1, 2, Direction.DESC, "createdAt");
    pagedEntities = ((EventCommentRepository) repository)
        .findByEventId(UUID.fromString("00000000-0000-0000-0002-000000000001"), paging);
    entities = pagedEntities.getContent();

    assertEquals(2, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), entities.get(0).getId());

    // Not found
    pagedEntities = ((EventCommentRepository) repository)
        .findByEventId(UUID.fromString("00000000-0000-0000-0002-000000000009"), null);
    entities = pagedEntities.getContent();
    assertEquals(0, entities.size());
  }
}
