package com.doppler.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.doppler.entities.User;

/**
 * The repository provides operations on User entity.
 */
@Repository
public interface UserRepository extends BaseRepository<User> {

  /**
   * Find a user by email.
   * 
   * @param email the email
   * @return the user, null if not found
   */
  User findByEmailIgnoreCase(String email);

  /**
   * Search users by full name.
   * 
   * @param fullName the full name
   * @return the users
   */
  List<User> findByFullNameContainingIgnoreCase(String fullName);

  /**
   * Search users by full name or email.
   * 
   * @param fullName the full name
   * @param email the email
   * @param pageable the paging criteria
   * @return the paged result
   */
  Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName,
      String email, Pageable pageable);

  /**
   * Find users by ids.
   * 
   * @param ids the ids
   * @return the users
   */
  List<User> findByIdIn(List<UUID> ids);
}
