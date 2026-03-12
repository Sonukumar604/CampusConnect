
package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.BookmarkType;
import lombok.Data;

@Data
public class CreateBookmarkDTO {

    private Long entityId;
    private BookmarkType type;

}