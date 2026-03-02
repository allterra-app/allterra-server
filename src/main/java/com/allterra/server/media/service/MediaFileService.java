package com.allterra.server.media.service;

import com.allterra.server.media.dto.MediaFileContentDto;
import com.allterra.server.media.dto.MediaFileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Media file service.
 */
@Service
@RequiredArgsConstructor
public class MediaFileService {
    private final MediaStorage mediaStorage;

    /**
     * Saves uploaded file in configured media storage and returns metadata.
     *
     * @param file multipart file
     * @return stored metadata
     */
    public MediaFileResponseDto upload(final MultipartFile file) {
        return upload(file.getOriginalFilename(), file.getContentType(), readContent(file), null);
    }

    /**
     * Saves uploaded file bytes in configured media storage and returns metadata.
     *
     * @param fileName source file name
     * @param contentType source content type
     * @param bytes source bytes
     * @return stored metadata
     */
    public MediaFileResponseDto upload(final String fileName, final String contentType, final byte[] bytes) {
        return upload(fileName, contentType, bytes, null);
    }

    /**
     * Saves uploaded file bytes with owner metadata in configured media storage and returns metadata.
     *
     * @param fileName source file name
     * @param contentType source content type
     * @param bytes source bytes
     * @param ownerEmail owner email
     * @return stored metadata
     */
    public MediaFileResponseDto upload(
            final String fileName,
            final String contentType,
            final byte[] bytes,
            final String ownerEmail
    ) {
        return mediaStorage.upload(fileName, contentType, bytes, ownerEmail);
    }

    /**
     * Gets file content by id and validates viewer access.
     *
     * @param id file id
     * @param viewerEmail current user email
     * @param isAdmin whether current user has admin role
     * @return file content
     */
    public MediaFileContentDto getByIdForViewer(final UUID id, final String viewerEmail, final boolean isAdmin) {
        final var file = getById(id);
        if (isAdmin) {
            return file;
        }

        if (viewerEmail == null || viewerEmail.isBlank()) {
            throw new AccessDeniedException("Authenticated user email is required for media access");
        }

        if (file.getOwnerEmail() == null || file.getOwnerEmail().isBlank()) {
            throw new AccessDeniedException("Media file does not have owner metadata");
        }

        if (!file.getOwnerEmail().equalsIgnoreCase(viewerEmail)) {
            throw new AccessDeniedException("Access to media file is denied");
        }

        return file;
    }

    /**
     * Gets file content and metadata from configured storage by id.
     *
     * @param id file id
     * @return file
     */
    public MediaFileContentDto getById(final UUID id) {
        return mediaStorage.getById(id);
    }

    private byte[] readContent(final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException("Cannot read uploaded file");
        }
    }
}
