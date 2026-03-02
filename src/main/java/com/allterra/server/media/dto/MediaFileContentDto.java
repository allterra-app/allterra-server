package com.allterra.server.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Media file content dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFileContentDto {
    private java.util.UUID id;
    private String fileName;
    private String contentType;
    private Long size;
    private String ownerEmail;
    private byte[] content;
}
