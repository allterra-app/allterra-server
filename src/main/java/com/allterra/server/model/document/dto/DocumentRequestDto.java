package com.allterra.server.model.document.dto;

import com.allterra.server.model.document.DocumentType;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for document.
 */
@Data
public class DocumentRequestDto {
    private String title;
    private DocumentType type;
    private UUID fileId;
    private UUID tripId;
    private String metadata;
}
