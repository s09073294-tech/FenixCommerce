package com.fenix.commerce.dto.order;

import com.fenix.commerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for order response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;
    private UUID orgId;
    private UUID websiteId;
    private String externalOrderId;
    private String externalOrderNumber;
    private Order.OrderStatus status;
    private Order.FinancialStatus financialStatus;
    private Order.FulfillmentOverallStatus fulfillmentStatus;
    private String customerEmail;
    private BigDecimal orderTotal;
    private String currency;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderUpdatedAt;
    private Instant ingestedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
