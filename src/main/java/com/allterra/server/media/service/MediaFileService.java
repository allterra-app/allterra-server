package com.allterra.server.media.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.media.dto.MediaFileContentDto;
import com.allterra.server.media.dto.MediaFileResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

/**
 * Media file service.
 */
@Service
public class MediaFileService {
    private static final String METADATA_EXTENSION = ".metadata";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String DEFAULT_FILE_NAME = "file";
    private static final String METADATA_FILE_NAME = "fileName";
    private static final String METADATA_CONTENT_TYPE = "contentType";
    private static final String METADATA_SIZE = "size";
    private static final String METADATA_OWNER_EMAIL = "ownerEmail";

    private final Path storageRootPath;

    public MediaFileService(@Value("${allterra.media.local-storage-path:./uploads}") final String storagePath) {
        this.storageRootPath = Paths.get(storagePath).toAbsolutePath().normalize();
        initializeStorageDirectory();
    }

    /**
     * Saves uploaded file in local storage and returns metadata.
     *
     * @param file multipart file
     * @return stored metadata
     */
    public MediaFileResponseDto upload(final MultipartFile file) {
        return upload(file.getOriginalFilename(), file.getContentType(), readContent(file), null);
    }

    /**
     * Saves uploaded file bytes in local storage and returns metadata.
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
     * Saves uploaded file bytes with owner metadata in local storage and returns metadata.
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
        final var id = UUID.randomUUID();
        final var sanitizedFileName = sanitizeFileName(fileName);
        final var normalizedContentType = normalizeContentType(contentType);
        final var normalizedOwnerEmail = normalizeOwnerEmail(ownerEmail);
        final var filePath = filePath(id);
        final var metadataPath = metadataPath(id);

        try {
            Files.write(filePath, bytes);
            writeMetadata(metadataPath, sanitizedFileName, normalizedContentType, bytes.length, normalizedOwnerEmail);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot store uploaded media file", exception);
        }

        return MediaFileResponseDto.builder()
                .id(id)
                .fileName(sanitizedFileName)
                .contentType(normalizedContentType)
                .size((long) bytes.length)
                .url("/api/v1/files/" + id)
                .build();
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
     * Gets file content and metadata from local storage by id.
     *
     * @param id file id
     * @return file
     */
    public MediaFileContentDto getById(final UUID id) {
        final var filePath = filePath(id);
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException(String.format("Media file with id %s not found", id));
        }

        try {
            final var bytes = Files.readAllBytes(filePath);
            final var metadata = readMetadata(id);
            final var fileName = metadata.getProperty(METADATA_FILE_NAME, id.toString());
            final var contentType = metadata.getProperty(METADATA_CONTENT_TYPE, detectContentType(filePath));
            final var size = Long.parseLong(metadata.getProperty(METADATA_SIZE, String.valueOf(bytes.length)));
            final var ownerEmail = metadata.getProperty(METADATA_OWNER_EMAIL);

            return MediaFileContentDto.builder()
                    .id(id)
                    .fileName(fileName)
                    .contentType(contentType)
                    .size(size)
                    .ownerEmail(ownerEmail)
                    .content(bytes)
                    .build();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read media file from storage", exception);
        }
    }

    private void initializeStorageDirectory() {
        try {
            Files.createDirectories(storageRootPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot initialize media storage directory", exception);
        }
    }

    private byte[] readContent(final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException("Cannot read uploaded file");
        }
    }

    private Path filePath(final UUID id) {
        return storageRootPath.resolve(id.toString()).normalize();
    }

    private Path metadataPath(final UUID id) {
        return storageRootPath.resolve(id + METADATA_EXTENSION).normalize();
    }

    private void writeMetadata(
            final Path metadataPath,
            final String fileName,
            final String contentType,
            final int size,
            final String ownerEmail
    ) throws IOException {
        final var metadata = new Properties();
        metadata.setProperty(METADATA_FILE_NAME, fileName);
        metadata.setProperty(METADATA_CONTENT_TYPE, contentType);
        metadata.setProperty(METADATA_SIZE, String.valueOf(size));
        if (ownerEmail != null) {
            metadata.setProperty(METADATA_OWNER_EMAIL, ownerEmail);
        }

        try (OutputStream stream = Files.newOutputStream(metadataPath)) {
            metadata.store(stream, null);
        }
    }

    private Properties readMetadata(final UUID id) throws IOException {
        final var metadata = new Properties();
        final var metadataPath = metadataPath(id);
        if (!Files.exists(metadataPath) || !Files.isRegularFile(metadataPath)) {
            return metadata;
        }

        try (InputStream stream = Files.newInputStream(metadataPath)) {
            metadata.load(stream);
        }
        return metadata;
    }

    private String sanitizeFileName(final String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return DEFAULT_FILE_NAME;
        }

        final var sanitized = fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_")
                .trim();
        return sanitized.isBlank() ? DEFAULT_FILE_NAME : sanitized;
    }

    private String normalizeContentType(final String contentType) {
        return contentType == null || contentType.isBlank() ? DEFAULT_CONTENT_TYPE : contentType;
    }

    private String normalizeOwnerEmail(final String ownerEmail) {
        if (ownerEmail == null || ownerEmail.isBlank()) {
            return null;
        }
        return ownerEmail.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private String detectContentType(final Path filePath) {
        try {
            final var detected = Files.probeContentType(filePath);
            return detected == null || detected.isBlank() ? DEFAULT_CONTENT_TYPE : detected;
        } catch (IOException exception) {
            return DEFAULT_CONTENT_TYPE;
        }
    }
}
