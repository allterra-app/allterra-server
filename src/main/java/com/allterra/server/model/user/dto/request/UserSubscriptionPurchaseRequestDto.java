package com.allterra.server.model.user.dto.request;

import com.allterra.server.model.user.SubscriptionPlan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user subscription purchase.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscriptionPurchaseRequestDto {

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan plan;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;
}
