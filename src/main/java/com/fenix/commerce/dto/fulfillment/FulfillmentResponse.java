package com.fenix.commerce.dto.fulfillment;

import com.fenix.commerce.entity.Fulfillment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FulfillmentResponse {

    private UUID id;
    private UUID orderId;
    private String externalFulfillmentId;
    private Fulfillment.FulfillmentStatus status;
    private String carrier;
    private String serviceLevel;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private Instant createdAt;
    private Instant updatedAt;
}
