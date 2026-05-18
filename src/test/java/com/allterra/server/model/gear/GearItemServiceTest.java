package com.allterra.server.model.gear;

import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import com.allterra.server.model.gear.dto.GearRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GearItemServiceTest {

    @Mock
    private GearItemRepository gearItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GearItemService gearItemService;

    @Test
    void testCreateGearItem() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(gearItemRepository.save(any())).thenReturn(GearItem.builder().id(UUID.randomUUID()).user(new User()).build());
        
        GearRequestDto request = new GearRequestDto();
        request.setName("Test");
        request.setCategory("Category");
        request.setWeightKg(BigDecimal.ONE);
        request.setStatus(GearStatus.GOOD);
        
        gearItemService.createGearItem(userId, request);
    }
}
