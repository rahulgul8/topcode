package com.doppler.repositories;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.doppler.entities.Event;

/**
 * The repository provides operations on Event entity.
 */
@Repository
public interface EventRepository extends BaseRepository<Event> {

  /**
   * Search events.
   * 
   * @param topicId the topic id
   * @param lastSessionEndFrom the last session end from, used to filter UPCOMING events
   * @param lastSessionEndTo the last session end to, used to filter PAST events
   * @param pageable the paging criteria
   * @return the paged result
   */
  @Query("SELECT e FROM Event e " //
      + "WHERE (:topicId IS NULL OR e.topicId = :topicId) " //
      + "AND (cast(:lastSessionEndFrom as timestamp) IS NULL OR e.lastSessionEnd >= :lastSessionEndFrom) " //
      + "AND (cast(:lastSessionEndTo as timestamp) IS NULL OR e.lastSessionEnd < :lastSessionEndTo) " //
  )
  Page<Event> search(@Param("topicId") String topicId,
      @Param("lastSessionEndFrom") Date lastSessionEndFrom,
      @Param("lastSessionEndTo") Date lastSessionEndTo, Pageable pageable);

  /**
   * Search events within specified event ids.
   * 
   * @param topicId the topic id
   * @param lastSessionEndFrom the last session end from, used to filter UPCOMING events
   * @param lastSessionEndTo the last session end to, used to filter PAST events
   * @param eventIds the event ids
   * @param pageable the paging criteria
   * @return the paged result
   */
  @Query("SELECT e FROM Event e " //
      + "WHERE (:topicId IS NULL OR e.topicId = :topicId) " //
      + "AND (cast(:lastSessionEndFrom as timestamp) IS NULL OR e.lastSessionEnd >= :lastSessionEndFrom) " //
      + "AND (cast(:lastSessionEndTo as timestamp) IS NULL OR e.lastSessionEnd < :lastSessionEndTo) " //
      + "AND (e.id IN (:eventIds)) " //
  )
  Page<Event> search(@Param("topicId") String topicId,
      @Param("lastSessionEndFrom") Date lastSessionEndFrom,
      @Param("lastSessionEndTo") Date lastSessionEndTo, @Param("eventIds") List<UUID> eventIds,
      Pageable pageable);
}
