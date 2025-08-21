package org.stark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stark.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find by email
    Optional<User> findByEmail(String email);

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by either username or email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Exists check for uniqueness validation
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Delete operations
    int deleteByEmail(String email);
    int deleteByUsername(String username);
}
