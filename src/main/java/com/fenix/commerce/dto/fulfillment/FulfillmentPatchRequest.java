package com.fenix.commerce.dto.fulfillment;

import com.fenix.commerce.entity.Fulfillment;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FulfillmentPatchRequest {

    private Fulfillment.FulfillmentStatus status;

    @Size(max = 64, message = "Carrier must not exceed 64 characters")
    private String carrier;

    @Size(max = 64, message = "Service level must not exceed 64 characters")
    private String serviceLevel;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;
}
