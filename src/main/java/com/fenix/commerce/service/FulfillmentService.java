package com.fenix.commerce.service;

import com.fenix.commerce.dto.PagedResponse;
import com.fenix.commerce.dto.fulfillment.FulfillmentCreateRequest;
import com.fenix.commerce.dto.fulfillment.FulfillmentPatchRequest;
import com.fenix.commerce.dto.fulfillment.FulfillmentResponse;
import com.fenix.commerce.entity.Fulfillment;
import com.fenix.commerce.entity.Order;
import com.fenix.commerce.exception.ResourceNotFoundException;
import com.fenix.commerce.repository.FulfillmentRepository;
import com.fenix.commerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public FulfillmentResponse createFulfillment(UUID orderId, FulfillmentCreateRequest request) {
        log.info("Creating fulfillment for order ID: {}", orderId);

        // Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        // Check if fulfillment already exists (prevent duplicates)
        boolean exists = fulfillmentRepository.existsByTenantTenantIdAndOrderOrderIdAndExternalFulfillmentId(
                order.getTenant().getTenantId(), orderId, request.getExternalFulfillmentId());

        if (exists) {
            throw new IllegalArgumentException(
                    "Fulfillment already exists with external ID: " + request.getExternalFulfillmentId());
        }

        // Create fulfillment
        Fulfillment fulfillment = Fulfillment.builder()
                .tenant(order.getTenant())
                .order(order)
                .externalFulfillmentId(request.getExternalFulfillmentId())
                .fulfillmentStatus(
                        request.getStatus() != null ? request.getStatus() : Fulfillment.FulfillmentStatus.UNKNOWN)
                .carrier(request.getCarrier())
                .serviceLevel(request.getServiceLevel())
                .shippedAt(request.getShippedAt())
                .deliveredAt(request.getDeliveredAt())
                .build();

        fulfillment = fulfillmentRepository.save(fulfillment);
        log.info("Successfully created fulfillment with ID: {}", fulfillment.getFulfillmentId());

        return mapToResponse(fulfillment);
    }


    public FulfillmentResponse getFulfillmentById(UUID orderId, UUID fulfillmentId) {
        log.debug("Fetching fulfillment with ID: {}", fulfillmentId);

        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> ResourceNotFoundException.fulfillment(fulfillmentId));

        // Validate it belongs to the order
        if (!fulfillment.getOrder().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Fulfillment does not belong to the specified order");
        }

        return mapToResponse(fulfillment);
    }

    public PagedResponse<FulfillmentResponse> listFulfillments(
            UUID orderId,
            Fulfillment.FulfillmentStatus status,
            String carrier,
            Instant from,
            Instant to,
            Pageable pageable) {

        log.debug("Listing fulfillments for order: {}", orderId);

        // Verify order exists
        if (!orderRepository.existsById(orderId)) {
            throw ResourceNotFoundException.order(orderId);
        }

        Page<Fulfillment> fulfillmentPage = fulfillmentRepository.searchFulfillments(
                orderId, status, carrier, from, to, pageable);

        return mapToPagedResponse(fulfillmentPage);
    }

    /**
     * Search by external fulfillment ID
     */
    public PagedResponse<FulfillmentResponse> searchByExternalId(
            UUID orderId,
            String externalFulfillmentId,
            Pageable pageable) {

        log.debug("Searching fulfillments by external ID: {}", externalFulfillmentId);

        Page<Fulfillment> fulfillmentPage = fulfillmentRepository.searchByExternalId(
                orderId, externalFulfillmentId, pageable);

        return mapToPagedResponse(fulfillmentPage);
    }

    /**
     * Update fulfillment (full update)
     */
    @Transactional
    public FulfillmentResponse updateFulfillment(
            UUID orderId,
            UUID fulfillmentId,
            FulfillmentCreateRequest request) {

        log.info("Updating fulfillment with ID: {}", fulfillmentId);

        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> ResourceNotFoundException.fulfillment(fulfillmentId));

        if (!fulfillment.getOrder().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Fulfillment does not belong to the specified order");
        }

        updateFulfillmentFields(fulfillment, request);
        fulfillment = fulfillmentRepository.save(fulfillment);

        return mapToResponse(fulfillment);
    }

    /**
     * Patch fulfillment (partial update)
     */
    @Transactional
    public FulfillmentResponse patchFulfillment(
            UUID orderId,
            UUID fulfillmentId,
            FulfillmentPatchRequest request) {

        log.info("Patching fulfillment with ID: {}", fulfillmentId);

        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> ResourceNotFoundException.fulfillment(fulfillmentId));

        if (!fulfillment.getOrder().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Fulfillment does not belong to the specified order");
        }

        patchFulfillmentFields(fulfillment, request);
        fulfillment = fulfillmentRepository.save(fulfillment);

        return mapToResponse(fulfillment);
    }

    /**
     * Delete fulfillment
     */
    @Transactional
    public void deleteFulfillment(UUID orderId, UUID fulfillmentId) {
        log.info("Deleting fulfillment with ID: {}", fulfillmentId);

        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() -> ResourceNotFoundException.fulfillment(fulfillmentId));

        if (!fulfillment.getOrder().getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Fulfillment does not belong to the specified order");
        }

        fulfillmentRepository.deleteById(fulfillmentId);
        log.info("Successfully deleted fulfillment with ID: {}", fulfillmentId);
    }

    // Helper methods

    private void updateFulfillmentFields(Fulfillment fulfillment, FulfillmentCreateRequest request) {
        fulfillment.setFulfillmentStatus(
                request.getStatus() != null ? request.getStatus() : Fulfillment.FulfillmentStatus.UNKNOWN);
        fulfillment.setCarrier(request.getCarrier());
        fulfillment.setServiceLevel(request.getServiceLevel());
        fulfillment.setShippedAt(request.getShippedAt());
        fulfillment.setDeliveredAt(request.getDeliveredAt());
    }

    private void patchFulfillmentFields(Fulfillment fulfillment, FulfillmentPatchRequest request) {
        if (request.getStatus() != null) {
            fulfillment.setFulfillmentStatus(request.getStatus());
        }
        if (request.getCarrier() != null) {
            fulfillment.setCarrier(request.getCarrier());
        }
        if (request.getServiceLevel() != null) {
            fulfillment.setServiceLevel(request.getServiceLevel());
        }
        if (request.getShippedAt() != null) {
            fulfillment.setShippedAt(request.getShippedAt());
        }
        if (request.getDeliveredAt() != null) {
            fulfillment.setDeliveredAt(request.getDeliveredAt());
        }
    }

    private FulfillmentResponse mapToResponse(Fulfillment fulfillment) {
        return FulfillmentResponse.builder()
                .id(fulfillment.getFulfillmentId())
                .orderId(fulfillment.getOrder().getOrderId())
                .externalFulfillmentId(fulfillment.getExternalFulfillmentId())
                .status(fulfillment.getFulfillmentStatus())
                .carrier(fulfillment.getCarrier())
                .serviceLevel(fulfillment.getServiceLevel())
                .shippedAt(fulfillment.getShippedAt())
                .deliveredAt(fulfillment.getDeliveredAt())
                .createdAt(fulfillment.getCreatedAt())
                .updatedAt(fulfillment.getUpdatedAt())
                .build();
    }

    private PagedResponse<FulfillmentResponse> mapToPagedResponse(Page<Fulfillment> fulfillmentPage) {
        return PagedResponse.<FulfillmentResponse>builder()
                .data(fulfillmentPage.getContent().stream()
                        .map(this::mapToResponse)
                        .toList())
                .page(fulfillmentPage.getNumber())
                .size(fulfillmentPage.getSize())
                .totalElements(fulfillmentPage.getTotalElements())
                .totalPages(fulfillmentPage.getTotalPages())
                .hasNext(fulfillmentPage.hasNext())
                .build();
    }
}
