package com.allterra.server.photo.controller;

import com.allterra.server.model.poi.Poi;
import com.allterra.server.photo.dto.PoiPhotoResponseDto;
import com.allterra.server.photo.dto.request.create.PoiPhotoCreateRequestDto;
import com.allterra.server.photo.dto.request.update.PoiPhotoUpdateRequestDto;
import com.allterra.server.photo.model.PoiPhoto;
import com.allterra.server.photo.service.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for {@link Poi}.
 */
@RestController
@RequestMapping("/poi-photos")
public class PoiPhotoController extends
        PhotoController<PoiPhoto, PoiPhotoResponseDto, PoiPhotoCreateRequestDto, PoiPhotoUpdateRequestDto> {
    public PoiPhotoController(
            final PhotoService<PoiPhoto, PoiPhotoResponseDto, PoiPhotoCreateRequestDto, PoiPhotoUpdateRequestDto> service
    ) {
        super(service);
    }
}
