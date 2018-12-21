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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import com.doppler.entities.Notification;

/**
 * The tests for NotificationRepository.
 */
public class NotificationRepositoryTest extends BaseRepositoryTest<Notification> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    Notification entity = new Notification();
    entity.setContent("content");
    entity.setIconUrl("icon url");
    entity.setRead(false);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity = repository.saveAndFlush(entity);

    Notification dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setContent("content 1");
    entity.setIconUrl("icon url 1");
    entity.setRead(true);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);
  }

  /**
   * Negative test for save() with null content.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullContent() {
    Notification entity = new Notification();
    entity.setIconUrl("icon url");
    entity.setRead(false);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with empty content.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createEmptyContent() {
    Notification entity = new Notification();
    entity.setContent("");
    entity.setIconUrl("icon url");
    entity.setRead(false);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    Notification entity = new Notification();
    entity.setContent("content");
    entity.setIconUrl("icon url");
    entity.setRead(false);

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non-existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNotExistedUserId() {
    Notification entity = new Notification();
    entity.setContent("content");
    entity.setIconUrl("icon url");
    entity.setRead(false);
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    Notification entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), entity.getUserId());
    assertEquals("icon 2", entity.getIconUrl());
    assertEquals("content 2", entity.getContent());
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
   * Positive test for findByUserId().
   */
  @Test
  public void findByUserId() {
    // Found
    Page<Notification> pagedEntities = ((NotificationRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"), null);
    List<Notification> entities = pagedEntities.getContent();

    assertEquals(3, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals("icon 1", entities.get(0).getIconUrl());
    assertEquals("content 1", entities.get(0).getContent());
    assertEquals("2018-10-01", String.format("%tY-%<tm-%<td", entities.get(0).getCreatedAt()));

    // Sort by createdAt desc
    Pageable paging = PageRequest.of(1, 2, Direction.DESC, "createdAt");
    pagedEntities = ((NotificationRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"), paging);
    entities = pagedEntities.getContent();

    assertEquals(1, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());

    // Not found
    pagedEntities = ((NotificationRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"), null);
    entities = pagedEntities.getContent();
    assertEquals(0, entities.size());
  }
}
