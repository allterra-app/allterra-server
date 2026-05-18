package com.allterra.server.model.document.dto;

import com.allterra.server.model.document.DocumentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for document.
 */
@Data
@Builder
public class DocumentResponseDto {
    private UUID id;
    private String title;
    private DocumentType type;
    private UUID fileId;
    private UUID tripId;
    private String fileUrl;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
