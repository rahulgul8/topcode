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
import com.doppler.entities.UserEvent;

/**
 * The tests for UserEventRepository.
 */
public class UserEventRepositoryTest extends BaseRepositoryTest<UserEvent> {

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
    UserEvent entity = new UserEvent();
    entity.setEventId(UUID.fromString("00000000-0000-0000-0002-000000000001"));
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000004")));
    entity = repository.saveAndFlush(entity);

    UserEvent dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity.getEventId(), dbEntity.getEventId());
    assertEquals(entity.getUser().getId(), dbEntity.getUser().getId());
    assertEquals(entity.isTicketScanned(), dbEntity.isTicketScanned());

    // Update
    entity.setEventId(UUID.fromString("00000000-0000-0000-0002-000000000002"));
    entity.setTicketScanned(true);
    repository.saveAndFlush(entity);

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity.getEventId(), dbEntity.getEventId());
    assertEquals(entity.getUser().getId(), dbEntity.getUser().getId());
    assertEquals(entity.isTicketScanned(), dbEntity.isTicketScanned());
  }

  /**
   * Negative test for save() with null eventId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullEventId() {
    UserEvent entity = new UserEvent();
    entity.setUser(userRepository.getOne(UUID.fromString("00000000-0000-0000-0000-000000000004")));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with null user.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUser() {
    UserEvent entity = new UserEvent();
    entity.setEventId(UUID.fromString("00000000-0000-0000-0001-000000000001"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UserEvent entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUser().getId());
    assertEquals(false, entity.isTicketScanned());
    assertEquals(UUID.fromString("00000000-0000-0000-0002-000000000002"), entity.getEventId());
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
    List<UserEvent> entities = ((UserEventRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    assertEquals(3, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals(UUID.fromString("00000000-0000-0000-0002-000000000001"),
        entities.get(0).getEventId());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"),
        entities.get(0).getUser().getId());
    assertEquals(true, entities.get(0).isTicketScanned());

    // Not found
    entities = ((UserEventRepository) repository)
        .findByUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));
    assertEquals(0, entities.size());
  }

  /**
   * Positive test for countByEventId().
   */
  @Test
  public void countByEventId() {
    long count = ((UserEventRepository) repository)
        .countByEventId(UUID.fromString("00000000-0000-0000-0002-000000000001"));
    assertEquals(3, count);
  }

  /**
   * Positive test for findByEventId().
   */
  @Test
  public void findByEventId() {
    // Found
    List<UserEvent> entities = ((UserEventRepository) repository)
        .findByEventId(UUID.fromString("00000000-0000-0000-0002-000000000001"));

    assertEquals(3, entities.size());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entities.get(0).getId());
    assertEquals(UUID.fromString("00000000-0000-0000-0002-000000000001"),
        entities.get(0).getEventId());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"),
        entities.get(0).getUser().getId());
    assertEquals(true, entities.get(0).isTicketScanned());

    // Not found
    entities = ((UserEventRepository) repository)
        .findByEventId(UUID.fromString("00000000-0000-0000-0002-000000000009"));
    assertEquals(0, entities.size());
  }
}
