package com.allterra.server.media.service;

import com.allterra.server.media.dto.MediaFileContentDto;
import com.allterra.server.media.dto.MediaFileResponseDto;

/**
 * Media storage abstraction.
 */
public interface MediaStorage {

    /**
     * Uploads media file bytes.
     *
     * @param fileName file name
     * @param contentType content type
     * @param bytes file bytes
     * @param ownerEmail owner email
     * @return uploaded metadata
     */
    MediaFileResponseDto upload(String fileName, String contentType, byte[] bytes, String ownerEmail);

    /**
     * Gets media file content by id.
     *
     * @param id media id
     * @return stored content with metadata
     */
    MediaFileContentDto getById(java.util.UUID id);
}
