package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByTitle(String title);
    List<Post> findByTitleContaining(String keyword);
    List<Post> findByUserId(Long userId);
    List<Post> findByCreatedDateAfter(LocalDateTime date);

}
