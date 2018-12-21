package com.doppler.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import com.doppler.entities.QuizAnswer;

/**
 * The tests for QuizAnswerRepository.
 */
public class QuizAnswerRepositoryTest extends BaseRepositoryTest<QuizAnswer> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    QuizAnswer entity = new QuizAnswer();
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedQuizAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity = repository.saveAll(Arrays.asList(entity)).get(0);

    QuizAnswer dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000006"));
    entity.setSelectedQuizAnswerOptionId(null);
    entity.setTextAnswer("text");
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAll(Arrays.asList(entity));

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Create in bulk
    List<QuizAnswer> entities = new ArrayList<>();

    entity = new QuizAnswer();
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedQuizAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entities.add(entity);

    entity = new QuizAnswer();
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedQuizAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entities.add(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    QuizAnswer entity = new QuizAnswer();
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedQuizAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non-existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNotExistedUserId() {
    QuizAnswer entity = new QuizAnswer();
    entity.setQuizQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedQuizAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    QuizAnswer entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUserId());
    assertEquals(UUID.fromString("00000000-0000-0000-0004-000000000002"),
        entity.getQuizQuestionId());
    assertEquals(UUID.fromString("00000000-0000-0000-0005-000000000002"),
        entity.getSelectedQuizAnswerOptionId());
    assertEquals(null, entity.getTextAnswer());
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
}
