package com.allterra.server.model.document;

import com.allterra.server.model.document.dto.DocumentRequestDto;
import com.allterra.server.model.document.dto.DocumentResponseDto;
import com.allterra.server.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for document management.
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    /**
     * Retrieves all documents for the current user.
     * @param userDetails authenticated user
     * @return list of documents
     */
    @GetMapping
    public List<DocumentResponseDto> getMyDocuments(@AuthenticationPrincipal final UserDetails userDetails) {
        UUID userId = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow().getId();
        return documentService.getUserDocuments(userId);
    }

    /**
     * Gets a document by ID.
     * @param id document id
     * @return document
     */
    @GetMapping("/{id}")
    public DocumentResponseDto getDocument(@PathVariable final UUID id) {
        return documentService.getDocument(id);
    }

    /**
     * Creates a new document.
     * @param userDetails authenticated user
     * @param request document details
     * @return created document
     */
    @PostMapping
    public DocumentResponseDto createDocument(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody final DocumentRequestDto request
    ) {
        UUID userId = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow().getId();
        return documentService.createDocument(userId, request);
    }

    /**
     * Deletes a document.
     * @param id document id
     * @return response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable final UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
