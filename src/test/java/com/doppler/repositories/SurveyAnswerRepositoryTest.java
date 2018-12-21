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
import com.doppler.entities.SurveyAnswer;

/**
 * The tests for SurveyAnswerRepository.
 */
public class SurveyAnswerRepositoryTest extends BaseRepositoryTest<SurveyAnswer> {

  /**
   * Positive tests for save().
   */
  @Test
  public void save() {
    // Create
    SurveyAnswer entity = new SurveyAnswer();
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedSurveyAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entity = repository.saveAll(Arrays.asList(entity)).get(0);

    SurveyAnswer dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Update
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000006"));
    entity.setSelectedSurveyAnswerOptionId(null);
    entity.setTextAnswer("text");
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    repository.saveAll(Arrays.asList(entity));

    dbEntity = repository.findById(entity.getId()).get();
    assertEquals(entity, dbEntity);

    // Create in bulk
    List<SurveyAnswer> entities = new ArrayList<>();

    entity = new SurveyAnswer();
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedSurveyAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entities.add(entity);

    entity = new SurveyAnswer();
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedSurveyAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    entities.add(entity);
  }

  /**
   * Negative test for save() with null userId.
   */
  @Test(expected = ConstraintViolationException.class)
  public void save_createNullUserId() {
    SurveyAnswer entity = new SurveyAnswer();
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedSurveyAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));

    repository.saveAndFlush(entity);
  }

  /**
   * Negative test for save() with non-existed userId.
   */
  @Test(expected = DataIntegrityViolationException.class)
  public void save_createNotExistedUserId() {
    SurveyAnswer entity = new SurveyAnswer();
    entity.setSurveyQuestionId(UUID.fromString("00000000-0000-0000-0004-000000000005"));
    entity.setSelectedSurveyAnswerOptionId(UUID.fromString("00000000-0000-0000-0005-000000000005"));
    entity.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000009"));

    repository.saveAndFlush(entity);
  }

  /**
   * Positive test for findById().
   */
  @Test
  public void findById() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    SurveyAnswer entity = repository.findById(id).get();

    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), entity.getUserId());
    assertEquals(UUID.fromString("00000000-0000-0000-0004-000000000002"),
        entity.getSurveyQuestionId());
    assertEquals(UUID.fromString("00000000-0000-0000-0005-000000000002"),
        entity.getSelectedSurveyAnswerOptionId());
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
