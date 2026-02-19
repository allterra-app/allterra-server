package com.allterra.server.model.poi;

import com.allterra.server.exception.ResourceNotFoundException;
import com.allterra.server.model.poi.dto.PoiResponseDto;
import com.allterra.server.model.poi.dto.request.PoiCreateRequestDto;
import com.allterra.server.model.poi.dto.request.PoiUpdateRequestDto;
import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoiServiceTest {

    @Mock
    private PoiRepository poiRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PoiMapper poiMapper;

    @InjectMocks
    private PoiService poiService;

    @Test
    void createPoiShouldMapAndSave() {
        var request = PoiCreateRequestDto.builder().name("poi").build();
        var entity = Poi.builder().name("poi").build();
        var saved = Poi.builder().id(com.allterra.server.TestUuids.id(1)).name("poi").build();
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).name("poi").build();

        when(poiMapper.toEntity(request)).thenReturn(entity);
        when(poiRepository.save(entity)).thenReturn(saved);
        when(poiMapper.toDto(saved)).thenReturn(response);

        assertThat(poiService.createPoi(request)).isEqualTo(response);
    }

    @Test
    void createPoiForUserShouldAddPoiToUser() {
        var request = PoiCreateRequestDto.builder().name("poi").build();
        var entity = Poi.builder().name("poi").build();
        var user = User.builder().pois(new ArrayList<>()).build();
        var response = PoiResponseDto.builder().id(com.allterra.server.TestUuids.id(1)).name("poi").build();

        when(poiMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.findById(com.allterra.server.TestUuids.id(5))).thenReturn(Optional.of(user));
        when(poiRepository.save(entity)).thenReturn(entity);
        when(poiMapper.toDto(entity)).thenReturn(response);

        var result = poiService.createPoiForUser(com.allterra.server.TestUuids.id(5), request);

        assertThat(result).isEqualTo(response);
        assertThat(user.getPois()).contains(entity);
    }

    @Test
    void getPoiShouldThrowWhenMissing() {
        when(poiRepository.findById(com.allterra.server.TestUuids.id(4))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poiService.getPoi(com.allterra.server.TestUuids.id(4)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Poi with id " + com.allterra.server.TestUuids.id(4) + " not found");
    }

    @Test
    void getPoiForUserShouldThrowWhenMissing() {
        when(userRepository.findById(com.allterra.server.TestUuids.id(1))).thenReturn(Optional.of(User.builder().pois(List.of()).build()));

        assertThatThrownBy(() -> poiService.getPoiForUser(com.allterra.server.TestUuids.id(1), com.allterra.server.TestUuids.id(2)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Poi with id " + com.allterra.server.TestUuids.id(2) + " not found for user "
                        + com.allterra.server.TestUuids.id(1));
    }

    @Test
    void getAllPoisForUserShouldReturnEmptyWhenUserMissing() {
        when(userRepository.findById(com.allterra.server.TestUuids.id(8))).thenReturn(Optional.empty());

        assertThat(poiService.getAllPoisForUser(com.allterra.server.TestUuids.id(8))).isEmpty();
    }

    @Test
    void updatePoiShouldThrowWhenMissing() {
        when(poiRepository.findById(com.allterra.server.TestUuids.id(7))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poiService.updatePoi(com.allterra.server.TestUuids.id(7), new PoiUpdateRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Poi with id " + com.allterra.server.TestUuids.id(7) + " not found");
    }

    @Test
    void deletePoiShouldDelegateToRepository() {
        poiService.deletePoi(com.allterra.server.TestUuids.id(7));

        verify(poiRepository).deleteById(com.allterra.server.TestUuids.id(7));
    }
}
