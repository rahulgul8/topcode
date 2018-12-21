package com.doppler.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The survey answer entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SurveyAnswer extends IdentifiableEntity {

  /**
   * The survey question id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID surveyQuestionId;

  /**
   * The selected survey answer option id.
   */
  @Column(columnDefinition = "uuid")
  private UUID selectedSurveyAnswerOptionId;

  /**
   * The text answer.
   */
  private String textAnswer;

  /**
   * The user id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  @JsonIgnore
  private UUID userId;
}
