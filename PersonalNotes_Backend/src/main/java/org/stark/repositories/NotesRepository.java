package org.stark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stark.entities.Notes;
import org.stark.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

    // Get all notes of a specific user
    List<Notes> findByUser(User user);

    // Find note by title (exact match) - risky if titles are not unique
    Optional<Notes> findByTitle(String title);

    // Find note by title for a specific user (safer than global search)
    Optional<Notes> findByUserAndTitle(User user, String title);

    // Search notes by title (case-insensitive) for a user
    List<Notes> findByUserAndTitleContainingIgnoreCase(User user, String keyword);

    // Delete by title (not recommended unless title is unique)
    int deleteByTitle(String title);

    // Delete all notes for a user
    int deleteByUser(User user);

    // Check if a note exists for a user with a specific title
    boolean existsByUserAndTitle(User user, String title);
}
