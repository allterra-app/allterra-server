package com.allterra.server.media.service;

import com.allterra.server.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MediaFileServiceTest {

    @TempDir
    Path storageDir;

    @Test
    void uploadShouldStoreFileInLocalStorageAndReturnDownloadMetadata() throws Exception {
        var service = new MediaFileService(new LocalMediaStorage(storageDir.toString()));
        var content = "photo-content".getBytes(UTF_8);
        var request = new MockMultipartFile("file", "trip photo.jpg", "image/jpeg", content);

        var response = service.upload(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getFileName()).isEqualTo("trip_photo.jpg");
        assertThat(response.getContentType()).isEqualTo("image/jpeg");
        assertThat(response.getSize()).isEqualTo((long) content.length);
        assertThat(response.getUrl()).isEqualTo("/api/v1/files/" + response.getId());

        var storedFilePath = storageDir.resolve(response.getId().toString());
        assertThat(Files.exists(storedFilePath)).isTrue();

        var storedContent = service.getById(response.getId());
        assertThat(storedContent.getFileName()).isEqualTo("trip_photo.jpg");
        assertThat(storedContent.getContentType()).isEqualTo("image/jpeg");
        assertThat(storedContent.getSize()).isEqualTo((long) content.length);
        assertThat(storedContent.getContent()).isEqualTo(content);
    }

    @Test
    void getByIdShouldThrowWhenFileDoesNotExist() {
        var service = new MediaFileService(new LocalMediaStorage(storageDir.toString()));

        assertThatThrownBy(() -> service.getById(com.allterra.server.TestUuids.id(999)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Media file with id");
    }
}
