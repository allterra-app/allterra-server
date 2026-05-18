package com.allterra.server.model.gear;

import com.allterra.server.model.user.User;
import com.allterra.server.model.user.UserRepository;
import com.allterra.server.model.gear.dto.GearRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "com.allterra.server")
class GearItemIntegrationTest {

    @Autowired
    private GearItemService gearItemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateGearItemIntegration() {
        User user = new User();
        user.setEmail("test@allterra.local");
        user = userRepository.save(user);
        
        GearRequestDto request = new GearRequestDto();
        request.setName("Test");
        request.setCategory("Category");
        request.setWeightKg(BigDecimal.ONE);
        request.setStatus(GearStatus.GOOD);
        
        gearItemService.createGearItem(user.getId(), request);
    }
}
