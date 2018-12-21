package com.doppler.entities.responses;

import java.util.ArrayList;
import java.util.List;
import com.doppler.entities.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The search response.
 * 
 * @param <T> the entity type
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SearchResponse<T extends IdentifiableEntity> {

  /**
   * The total results found.
   */
  private long count;

  /**
   * The results.
   */
  private List<T> rows = new ArrayList<>();
}
