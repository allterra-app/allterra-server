package com.allterra.server.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Media file response dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFileResponseDto {
    private java.util.UUID id;
    private String fileName;
    private String contentType;
    private Long size;
    private String url;
}
