
package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateBookmarkDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Bookmark;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.BookmarkRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @Override
    public Bookmark saveBookmark(Long userId, CreateBookmarkDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .entityId(dto.getEntityId())
                .type(dto.getType())
                .build();

        return bookmarkRepository.save(bookmark);
    }

    @Override
    public List<Bookmark> getUserBookmarks(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookmarkRepository.findByUser(user);
    }

    @Override
    public void deleteBookmark(Long bookmarkId) {

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }
}