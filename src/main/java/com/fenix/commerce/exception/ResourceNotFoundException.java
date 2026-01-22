package com.fenix.commerce.exception;

import java.util.UUID;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException order(UUID orderId) {
        return new ResourceNotFoundException("Order not found with ID: " + orderId);
    }

    public static ResourceNotFoundException fulfillment(UUID fulfillmentId) {
        return new ResourceNotFoundException("Fulfillment not found with ID: " + fulfillmentId);
    }

    public static ResourceNotFoundException tenant(UUID tenantId) {
        return new ResourceNotFoundException("Tenant not found with ID: " + tenantId);
    }

    public static ResourceNotFoundException store(UUID storeId) {
        return new ResourceNotFoundException("Store not found with ID: " + storeId);
    }

    public static ResourceNotFoundException tracking(UUID trackingId) {
        return new ResourceNotFoundException("Tracking not found with ID: " + trackingId);
    }
}
