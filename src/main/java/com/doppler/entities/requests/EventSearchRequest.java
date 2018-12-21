package com.doppler.entities.requests;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The event search request.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class EventSearchRequest extends PagingAndSortingSearchRequest {

  /**
   * The topic id filter.
   */
  private UUID topicId;

  /**
   * The event status filter.
   */
  private EventStatus status;

  /**
   * The onlyMyEvents filter. True to filter only the events to which the current users registered.
   * Default to false if not present.
   */
  private boolean onlyMyEvents;
}
