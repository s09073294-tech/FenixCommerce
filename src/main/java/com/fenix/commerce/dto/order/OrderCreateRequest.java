package com.fenix.commerce.dto.order;

import com.fenix.commerce.entity.Order;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating a new order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotNull(message = "Website ID is required")
    private UUID websiteId;

    @NotBlank(message = "External order ID is required")
    @Size(max = 128, message = "External order ID must not exceed 128 characters")
    private String externalOrderId;

    @Size(max = 128, message = "External order number must not exceed 128 characters")
    private String externalOrderNumber;

    private Order.OrderStatus status;

    private Order.FinancialStatus financialStatus;

    private Order.FulfillmentOverallStatus fulfillmentStatus;

    @Email(message = "Invalid email format")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    private String customerEmail;

    @DecimalMin(value = "0.0", inclusive = true, message = "Order total must be non-negative")
    private BigDecimal orderTotal;

    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
    private String currency;

    private LocalDateTime orderCreatedAt;

    private LocalDateTime orderUpdatedAt;
}
