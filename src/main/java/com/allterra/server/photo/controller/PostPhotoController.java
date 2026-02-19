package com.allterra.server.photo.controller;

import com.allterra.server.model.post.Post;
import com.allterra.server.photo.dto.PostPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PostPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PostPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PostPhoto;
import com.allterra.server.photo.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for {@link Post}.
 */
@RestController
@RequestMapping("/post-photos")
public class PostPhotoController extends
        PhotoController<PostPhoto, PostPhotoResponseDto, PostPhotoCreateRequestDto, PostPhotoUpdateRequestDto> {
    public PostPhotoController(
            final PhotoService<PostPhoto, PostPhotoResponseDto, PostPhotoCreateRequestDto, PostPhotoUpdateRequestDto> service
    ) {
        super(service);
    }
}
