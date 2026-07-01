package com.allterra.server.model.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated feed response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedPageResponseDto {
    private List<PostResponseDto> items;
    private int page;
    private int size;
    private long totalItems;
    private boolean hasNext;
}
