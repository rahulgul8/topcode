package com.doppler.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.doppler.entities.Event;
import com.doppler.entities.UserEvent;
import com.doppler.entities.requests.PagingAndSortingSearchRequest;
import com.doppler.repositories.EventRepository;
import com.doppler.repositories.UserEventRepository;
import com.doppler.security.SecurityUtils;

/**
 * The base class for all services.
 */
public abstract class BaseService {

  /**
   * The default value for search query offset.
   */
  private static final int DEFAULT_SEARCH_OFFSET = 0;

  /**
   * The default value for search query limit.
   */
  private static final int DEFAULT_SEARCH_LIMIT = Integer.MAX_VALUE;

  /**
   * The valid sort directions.
   */
  private static final List<String> VALID_SORT_DIRECTIONS = Arrays.asList("asc", "desc");

  /**
   * The event repository.
   */
  @Autowired
  private EventRepository eventRepository;

  /**
   * The user event repository.
   */
  @Autowired
  private UserEventRepository userEventRepository;

  /**
   * Create Spring page request.
   * 
   * @param criteria the criteria
   * @param validSortColumns the valid sort columns
   * @param defaultSortColumn the default sort column
   * @param defaultSortDirection the default sort direction
   * @return the Spring page request
   */
  protected <S extends PagingAndSortingSearchRequest> Pageable createPageRequest(S criteria,
      List<String> validSortColumns, String defaultSortColumn, String defaultSortDirection) {

    // Set default offset
    if (criteria.getOffset() == null) {
      criteria.setOffset(DEFAULT_SEARCH_OFFSET);
    }

    // Set default limit
    if (criteria.getLimit() == null) {
      criteria.setLimit(DEFAULT_SEARCH_LIMIT);
    }

    // Set default sort direction
    if (StringUtils.isEmpty(criteria.getSortDirection())) {
      criteria.setSortDirection(defaultSortDirection.toUpperCase());
    } else {
      // Validate sort orders
      validateStringInList(criteria.getSortDirection(), "sortDirection", VALID_SORT_DIRECTIONS);

      criteria.setSortDirection(criteria.getSortDirection().toUpperCase());
    }

    // Validate sort column and build the sort object
    if (StringUtils.isEmpty(criteria.getSortBy())) {
      criteria.setSortBy(defaultSortColumn);
    } else {
      validateStringInList(criteria.getSortBy(), "sortBy", validSortColumns);
    }

    Sort sort = Sort.by("ASC".equals(criteria.getSortDirection()) ? Direction.ASC : Direction.DESC,
        criteria.getSortBy());

    // Create the paging request
    Pageable pageable = new OffsetLimitPageRequest(criteria.getOffset(), criteria.getLimit(), sort);

    return pageable;
  }

  /**
   * Validate that a property value is in the specified list of strings.
   * 
   * @param propertyValue the property value
   * @param propertyName the property name
   * @param list the list
   * @throws IllegalArgumentException if the value is not in the specified list
   */
  protected void validateStringInList(Object propertyValue, String propertyName,
      List<String> list) {
    if (!list.contains(propertyValue)) {
      throw new IllegalArgumentException(String.format("%s must be in %s", propertyName, list));
    }
  }

  /**
   * Validate list is not empty or contains null value.
   * 
   * @param propertyValue the property value
   * @param propertyName the property name
   * @param list the list
   * @throws IllegalArgumentException if the value is not valid
   */
  protected void validateList(List<?> propertyValue, String propertyName) {
    if (CollectionUtils.isEmpty(propertyValue)) {
      throw new IllegalArgumentException(String.format("%s must not be empty", propertyName));
    }

    for (Object object : propertyValue) {
      if (StringUtils.isEmpty(object)) {
        throw new IllegalArgumentException(
            String.format("%s must not include null or empty value", propertyName));
      }
    }
  }

  /**
   * Get string value from a UUID.
   * 
   * @param uuid the uuid
   * @return the string
   */
  protected String uuidToString(UUID uuid) {
    return uuid == null ? null : uuid.toString();
  }

  /**
   * Get event by id.
   * 
   * @param eventRepository the repository
   * @param id the id
   * @return the event
   */
  protected Event getEventById(UUID id) {
    Optional<Event> optionalEvent = eventRepository.findById(id);

    if (!optionalEvent.isPresent()) {
      throw new EntityNotFoundException("Event does not exist with id = " + id);
    }

    Event event = optionalEvent.get();

    // Check if it's my event
    UserEvent userEvent = userEventRepository
        .findByUserIdAndEventId(SecurityUtils.getCurrentUser().getId(), event.getId());
    event.setIsMyEvent(userEvent != null);

    return event;
  }
  
	protected HttpEntity<String> constructHttpEntityWithRequestHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		return entity;
	}
}
