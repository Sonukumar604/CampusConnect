package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Bookmark;
import com.example.CampusConnect.model.BookmarkType;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Bookmark bookmark;

    @BeforeEach
    void setup() {

        user = User.builder()
                .name("Hero")
                .email("hero@gmail.com")
                .role(Role.STUDENT)
                .status(User.Status.ACTIVE)
                .createdBy("test-user")
                .updatedBy("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persist(user);
        entityManager.flush();   // 🔥 VERY IMPORTANT

        bookmark = Bookmark.builder()
                .user(user)
                .entityId(101L)
                .type(BookmarkType.COURSE)
                .build();
    }

    //  HAPPY CASE
    // Test findByUser() when bookmarks exist
    @Test
    void testFindByUser_whenBookmarksExist_thenReturnBookmarkList() {

        // Arrange
        bookmarkRepository.save(bookmark);

        // Act
        List<Bookmark> result = bookmarkRepository.findByUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEntityId()).isEqualTo(101L);
        assertThat(result.get(0).getType()).isEqualTo(BookmarkType.COURSE);
    }

    // SAD CASE
    // Test findByUser() when no bookmarks exist
    @Test
    void testFindByUser_whenNoBookmarks_thenReturnEmptyList() {

        // Act
        List<Bookmark> result = bookmarkRepository.findByUser(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}