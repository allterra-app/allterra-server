package com.allterra.server.media.service;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.media.dto.MediaFileContentDto;
import com.allterra.server.media.dto.MediaFileResponseDto;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * AWS S3 media storage.
 */
public class S3MediaStorage implements MediaStorage {
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String DEFAULT_FILE_NAME = "file";
    private static final String METADATA_FILE_NAME = "filename";
    private static final String METADATA_OWNER_EMAIL = "owneremail";
    private static final String METADATA_CONTENT_TYPE = "contenttype";

    private final S3Client s3Client;
    private final String bucket;
    private final String keyPrefix;

    public S3MediaStorage(final S3Client s3Client, final String bucket, final String keyPrefix) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.keyPrefix = normalizePrefix(keyPrefix);
    }

    @Override
    public MediaFileResponseDto upload(
            final String fileName,
            final String contentType,
            final byte[] bytes,
            final String ownerEmail
    ) {
        final var id = UUID.randomUUID();
        final var key = objectKey(id);
        final var normalizedFileName = sanitizeFileName(fileName);
        final var normalizedContentType = normalizeContentType(contentType);
        final var metadata = new HashMap<String, String>();
        metadata.put(METADATA_FILE_NAME, normalizedFileName);
        metadata.put(METADATA_CONTENT_TYPE, normalizedContentType);
        final var normalizedOwnerEmail = normalizeOwnerEmail(ownerEmail);
        if (normalizedOwnerEmail != null) {
            metadata.put(METADATA_OWNER_EMAIL, normalizedOwnerEmail);
        }

        final var request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(normalizedContentType)
                .metadata(metadata)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (S3Exception exception) {
            throw new IllegalStateException("Cannot store uploaded media file in S3", exception);
        }

        return MediaFileResponseDto.builder()
                .id(id)
                .fileName(normalizedFileName)
                .contentType(normalizedContentType)
                .size((long) bytes.length)
                .url("/api/v1/files/" + id)
                .build();
    }

    @Override
    public MediaFileContentDto getById(final UUID id) {
        final var request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey(id))
                .build();

        try {
            final var responseBytes = s3Client.getObjectAsBytes(request);
            final var response = responseBytes.response();
            final var metadata = response.metadata() == null ? Map.<String, String>of() : response.metadata();
            final var bytes = responseBytes.asByteArray();
            final var fileName = metadata.getOrDefault(METADATA_FILE_NAME, id.toString());
            final var contentType = normalizeContentType(
                    response.contentType() == null
                            ? metadata.getOrDefault(METADATA_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                            : response.contentType()
            );
            final var ownerEmail = metadata.get(METADATA_OWNER_EMAIL);
            final var size = response.contentLength() == null ? (long) bytes.length : response.contentLength();

            return MediaFileContentDto.builder()
                    .id(id)
                    .fileName(fileName)
                    .contentType(contentType)
                    .size(size)
                    .ownerEmail(ownerEmail)
                    .content(bytes)
                    .build();
        } catch (NoSuchKeyException exception) {
            throw new ResourceNotFoundException(String.format("Media file with id %s not found", id));
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                throw new ResourceNotFoundException(String.format("Media file with id %s not found", id));
            }
            throw new IllegalStateException("Cannot read media file from S3", exception);
        }
    }

    private String objectKey(final UUID id) {
        return keyPrefix + "/" + id;
    }

    private String normalizePrefix(final String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "media";
        }
        return prefix.trim().replaceAll("^/+", "").replaceAll("/+$", "");
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
        return ownerEmail.trim().toLowerCase(Locale.ROOT);
    }
}
