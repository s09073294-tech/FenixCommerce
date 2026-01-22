package com.fenix.commerce.dto.fulfillment;

import com.fenix.commerce.entity.Fulfillment;
import jakarta.validation.constraints.NotBlank;
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
public class FulfillmentCreateRequest {

    @NotBlank(message = "External fulfillment ID is required")
    @Size(max = 128, message = "External fulfillment ID must not exceed 128 characters")
    private String externalFulfillmentId;

    private Fulfillment.FulfillmentStatus status;

    @Size(max = 64, message = "Carrier must not exceed 64 characters")
    private String carrier;

    @Size(max = 64, message = "Service level must not exceed 64 characters")
    private String serviceLevel;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;
}
