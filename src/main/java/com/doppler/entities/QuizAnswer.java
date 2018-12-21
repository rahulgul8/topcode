package com.doppler.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The quiz answer entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QuizAnswer extends IdentifiableEntity {

  /**
   * The quiz question id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID quizQuestionId;

  /**
   * The selected survey answer option id.
   */
  @Column(columnDefinition = "uuid")
  private UUID selectedQuizAnswerOptionId;

  /**
   * The text answer.
   */
  private String textAnswer;

  /**
   * The user id.
   */
  @NotNull
  @Column(columnDefinition = "uuid")
  private UUID userId;
}
