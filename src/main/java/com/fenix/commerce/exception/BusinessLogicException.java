package com.fenix.commerce.exception;


public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }

    public static BusinessLogicException duplicateOrder(String externalOrderId) {
        return new BusinessLogicException("Order already exists with external ID: " + externalOrderId);
    }

    public static BusinessLogicException duplicateFulfillment(String externalFulfillmentId) {
        return new BusinessLogicException("Fulfillment already exists with external ID: " + externalFulfillmentId);
    }

    public static BusinessLogicException invalidTenant(String message) {
        return new BusinessLogicException("Invalid tenant: " + message);
    }
}
