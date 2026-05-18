package com.allterra.server.model.document;

import com.allterra.server.model.document.dto.DocumentRequestDto;
import com.allterra.server.model.document.dto.DocumentResponseDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing documents.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final com.allterra.server.model.user.UserRepository userRepository;

    /**
     * Gets documents for user.
     * @param userId user id
     * @return list of documents
     */
    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getUserDocuments(final UUID userId) {
        return documentRepository.findAllByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets a document.
     * @param id doc id
     * @return doc
     */
    @Transactional(readOnly = true)
    public DocumentResponseDto getDocument(final UUID id) {
        return documentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * Creates a document.
     * @param userId user id
     * @param request doc req
     * @return doc dto
     */
    @Transactional
    public DocumentResponseDto createDocument(final UUID userId, final DocumentRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        Document document = Document.builder()
                .title(request.getTitle())
                .type(request.getType())
                .fileId(request.getFileId())
                .tripId(request.getTripId())
                .user(user)
                .metadata(request.getMetadata())
                .build();

        return mapToDto(documentRepository.save(document));
    }

    /**
     * Deletes a document.
     * @param id doc id
     */
    @Transactional
    public void deleteDocument(final UUID id) {
        documentRepository.deleteById(id);
    }

    private DocumentResponseDto mapToDto(Document doc) {
        return DocumentResponseDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .type(doc.getType())
                .fileId(doc.getFileId())
                .tripId(doc.getTripId())
                .fileUrl("/api/v1/files/" + doc.getFileId())
                .metadata(doc.getMetadata())
                .createdAt(doc.getCreatedAt())
                .modifiedAt(doc.getModifiedAt())
                .build();
    }
}
