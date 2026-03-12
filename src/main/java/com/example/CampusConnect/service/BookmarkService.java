package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateBookmarkDTO;
import com.example.CampusConnect.model.Bookmark;

import java.util.List;

public interface BookmarkService {

    Bookmark saveBookmark(Long userId, CreateBookmarkDTO dto);

    List<Bookmark> getUserBookmarks(Long userId);

    void deleteBookmark(Long bookmarkId);

}