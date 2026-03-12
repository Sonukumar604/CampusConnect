
package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateBookmarkDTO;
import com.example.CampusConnect.model.Bookmark;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public Bookmark saveBookmark(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateBookmarkDTO dto
    ) {

        return bookmarkService.saveBookmark(user.getId(), dto);
    }

    @GetMapping
    public List<Bookmark> getBookmarks(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return bookmarkService.getUserBookmarks(user.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteBookmark(@PathVariable Long id) {

        bookmarkService.deleteBookmark(id);
    }
}