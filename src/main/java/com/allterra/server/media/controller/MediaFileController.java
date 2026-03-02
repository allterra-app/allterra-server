package com.allterra.server.media.controller;

import com.allterra.server.media.dto.MediaFileResponseDto;
import com.allterra.server.media.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Media file controller.
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class MediaFileController {
    private final MediaFileService mediaFileService;

    /**
     * Uploads binary file to media storage.
     *
     * @param file uploaded file
     * @return uploaded file metadata
     */
    @PostMapping("/upload")
    public ResponseEntity<MediaFileResponseDto> upload(
            @RequestParam("file") final MultipartFile file,
            final Authentication authentication
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        log.info("Upload file [{}], size [{}]", file.getOriginalFilename(), file.getSize());
        return ResponseEntity.ok(
                mediaFileService.upload(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        readBytes(file),
                        requireUserEmail(authentication)
                )
        );
    }

    /**
     * Uploads binary file bytes without multipart envelope.
     *
     * @param content raw file bytes
     * @param fileName file name
     * @param contentType content type
     * @return uploaded file metadata
     */
    @PostMapping("/upload/raw")
    public ResponseEntity<MediaFileResponseDto> uploadRaw(
            @RequestBody final byte[] content,
            @RequestParam(required = false) final String fileName,
            @RequestParam(required = false) final String contentType,
            final Authentication authentication
    ) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("File content is required");
        }
        log.info("Upload raw file [{}], size [{}]", fileName, content.length);
        return ResponseEntity.ok(mediaFileService.upload(fileName, contentType, content, requireUserEmail(authentication)));
    }

    /**
     * Downloads file by id.
     *
     * @param id file id
     * @return file bytes
     */
    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable final java.util.UUID id,
            final Authentication authentication
    ) {
        final var file = mediaFileService.getByIdForViewer(
                id,
                requireUserEmail(authentication),
                isAdmin(authentication)
        );
        final var mediaType = parseMediaType(file.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(file.getFileName() == null ? id + ".bin" : file.getFileName())
                                .build()
                                .toString()
                )
                .contentLength(file.getSize() == null ? file.getContent().length : file.getSize())
                .body(new ByteArrayResource(file.getContent()));
    }

    private String requireUserEmail(final Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        return authentication.getName();
    }

    private boolean isAdmin(final Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private byte[] readBytes(final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException("Cannot read uploaded file");
        }
    }

    private MediaType parseMediaType(final String raw) {
        try {
            return raw == null || raw.isBlank() ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(raw);
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
