
package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Bookmark;
import com.example.CampusConnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUser(User user);

}