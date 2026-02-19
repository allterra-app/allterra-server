package com.allterra.server.photo.controller;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.photo.dto.UserPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.UserPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.UserPhotoUpdateRequestDto;
import com.allterra.server.photo.model.UserPhoto;
import com.allterra.server.photo.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for {@link Poi}.
 */
@RestController
@RequestMapping("/poi-photos")
public class PoiPhotoController extends
        PhotoController<UserPhoto, UserPhotoResponseDto, UserPhotoCreateRequestDto, UserPhotoUpdateRequestDto> {
    public PoiPhotoController(
            final PhotoService<UserPhoto, UserPhotoResponseDto, UserPhotoCreateRequestDto, UserPhotoUpdateRequestDto> service
    ) {
        super(service);
    }
}
