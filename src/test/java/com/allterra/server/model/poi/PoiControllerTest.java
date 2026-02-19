package com.allterra.server.model.poi;

import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoiControllerTest {

    @Mock
    private PoiService poiService;

    @InjectMocks
    private PoiController poiController;

    @Test
    void createPoiShouldReturnOk() {
        var request = PoiCreateRequestDto.builder().name("poi").build();
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).name("poi").build();
        when(poiService.createPoi(request)).thenReturn(response);

        var result = poiController.createPoi(request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void createPoiForUserShouldReturnOk() {
        var request = PoiCreateRequestDto.builder().name("poi").build();
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).name("poi").build();
        when(poiService.createPoiForUser(com.allterra.server.TestUuids.id(2), request)).thenReturn(response);

        var result = poiController.createPoiForUser(com.allterra.server.TestUuids.id(2), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getPoiShouldReturnOk() {
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(3)).build();
        when(poiService.getPoi(com.allterra.server.TestUuids.id(3))).thenReturn(response);

        var result = poiController.getPoi(com.allterra.server.TestUuids.id(3));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllPoisShouldReturnAll() {
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).build();
        when(poiService.getAllPois()).thenReturn(List.of(response));

        var result = poiController.getAllPois();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getPoiForUserShouldReturnOk() {
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(4)).build();
        when(poiService.getPoiForUser(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(4))).thenReturn(response);

        var result = poiController.getPoiForUser(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(4));

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updatePoiShouldReturnUpdated() {
        var request = PoiUpdateRequestDto.builder().id(com.allterra.server.TestUuids.id(4)).name("updated").build();
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(4)).name("updated").build();
        when(poiService.updatePoi(com.allterra.server.TestUuids.id(4), request)).thenReturn(response);

        var result = poiController.updatePoi(com.allterra.server.TestUuids.id(4), request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deletePoiShouldReturnNoContent() {
        var result = poiController.deletePoi(com.allterra.server.TestUuids.id(5));

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(poiService).deletePoi(com.allterra.server.TestUuids.id(5));
    }
}
