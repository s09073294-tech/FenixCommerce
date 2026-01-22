package com.fenix.commerce.service;

import com.fenix.commerce.dto.PagedResponse;
import com.fenix.commerce.dto.order.OrderCreateRequest;
import com.fenix.commerce.dto.order.OrderPatchRequest;
import com.fenix.commerce.dto.order.OrderResponse;
import com.fenix.commerce.entity.Order;
import com.fenix.commerce.entity.Store;
import com.fenix.commerce.entity.Tenant;
import com.fenix.commerce.exception.ResourceNotFoundException;
import com.fenix.commerce.repository.OrderRepository;
import com.fenix.commerce.repository.StoreRepository;
import com.fenix.commerce.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service layer for Order operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;


@Transactional
public OrderResponse createOrder(OrderCreateRequest request) {
    log.info("Creating/upserting order with external ID: {}", request.getExternalOrderId());

    // 1️⃣ Validate tenant exists
    System.out.print(" tenant : "+tenantRepository.findById(request.getOrgId()));
    Tenant tenant = tenantRepository.findById(request.getOrgId())
            .orElseThrow(() -> ResourceNotFoundException.tenant(request.getOrgId()));


    // 2️⃣ Validate store exists and belongs to tenant
    System.out.print(" store : "+storeRepository.findById(request.getWebsiteId()));
    Store store = storeRepository.findById(request.getWebsiteId())
            .orElseThrow(() -> ResourceNotFoundException.store(request.getWebsiteId()));


    if (!store.getTenant().getTenantId().equals(request.getOrgId())) {
        throw new IllegalArgumentException("Store does not belong to the specified organization");
    }

    // 3️⃣ Check if order exists (upsert)
    Order order = orderRepository
            .findByTenantTenantIdAndStoreStoreIdAndExternalOrderId(
                    request.getOrgId(), request.getWebsiteId(), request.getExternalOrderId())
            .orElse(Order.builder()
                    .tenant(tenant)
                    .store(store)
                    .externalOrderId(request.getExternalOrderId())
                    .ingestedAt(Instant.now()) // set ingestedAt on new order
                    .build());

    // 4️⃣ Update order fields
    order.setExternalOrderNumber(request.getExternalOrderNumber());
    order.setOrderStatus(request.getStatus() != null ? request.getStatus() : Order.OrderStatus.CREATED);
    order.setFinancialStatus(request.getFinancialStatus() != null ? request.getFinancialStatus() : Order.FinancialStatus.UNKNOWN);
    order.setFulfillmentStatus(request.getFulfillmentStatus() != null ? request.getFulfillmentStatus() : Order.FulfillmentOverallStatus.UNKNOWN);
    order.setCustomerEmail(request.getCustomerEmail());
    order.setOrderTotalAmount(request.getOrderTotal() != null ? request.getOrderTotal() : BigDecimal.ZERO);
    order.setCurrency(request.getCurrency());
    order.setOrderCreatedAt(request.getOrderCreatedAt() != null ? request.getOrderCreatedAt() : LocalDateTime.now());
    order.setOrderUpdatedAt(request.getOrderUpdatedAt() != null ? request.getOrderUpdatedAt() : LocalDateTime.now());

    // 5️⃣ Save order
    order = orderRepository.save(order);
    log.info("Successfully saved order with ID: {}", order.getOrderId());

    return mapToResponse(order);
}

    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return mapToPagedResponse(orderPage);
    }


    public OrderResponse getOrderById(UUID orderId) {
        log.debug("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));
        return mapToResponse(order);
    }


    public PagedResponse<OrderResponse> searchOrders(
            UUID orgId,
            UUID websiteId,
            Order.OrderStatus status,
            Order.FinancialStatus financialStatus,
            Order.FulfillmentOverallStatus fulfillmentStatus,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        log.debug("Searching orders for org: {}, store: {}", orgId, websiteId);

        Page<Order> orderPage = orderRepository.searchOrders(
                orgId, websiteId, status, financialStatus, fulfillmentStatus, from, to, pageable);

        return mapToPagedResponse(orderPage);
    }


    public PagedResponse<OrderResponse> searchByExternalIds(
            UUID orgId,
            UUID websiteId,
            String externalOrderId,
            String externalOrderNumber,
            Pageable pageable) {

        log.debug("Searching orders by external IDs");

        Page<Order> orderPage = orderRepository.searchByExternalIds(
                orgId, websiteId, externalOrderId, externalOrderNumber, pageable);

        return mapToPagedResponse(orderPage);
    }


    @Transactional
    public OrderResponse updateOrder(UUID orderId, OrderCreateRequest request) {
        log.info("Updating order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        updateOrderFields(order, request);
        order = orderRepository.save(order);

        return mapToResponse(order);
    }


    @Transactional
    public OrderResponse patchOrder(UUID orderId, OrderPatchRequest request) {
        log.info("Patching order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        patchOrderFields(order, request);
        order = orderRepository.save(order);

        return mapToResponse(order);
    }


    @Transactional
    public void deleteOrder(UUID orderId) {
        log.info("Deleting order with ID: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw ResourceNotFoundException.order(orderId);
        }

        orderRepository.deleteById(orderId);
        log.info("Successfully deleted order with ID: {}", orderId);
    }

    // Helper methods
    private void updateOrderFields(Order order, OrderCreateRequest request) {
        order.setExternalOrderNumber(request.getExternalOrderNumber());
        order.setOrderStatus(request.getStatus() != null ? request.getStatus() : Order.OrderStatus.CREATED);
        order.setFinancialStatus(
                request.getFinancialStatus() != null ? request.getFinancialStatus() : Order.FinancialStatus.UNKNOWN);
        order.setFulfillmentStatus(request.getFulfillmentStatus() != null ? request.getFulfillmentStatus()
                : Order.FulfillmentOverallStatus.UNKNOWN);
        order.setCustomerEmail(request.getCustomerEmail());
        order.setOrderTotalAmount(request.getOrderTotal() != null ? request.getOrderTotal() : BigDecimal.ZERO);
        order.setCurrency(request.getCurrency());
        order.setOrderCreatedAt(request.getOrderCreatedAt());
        order.setOrderUpdatedAt(request.getOrderUpdatedAt());
    }

    private void patchOrderFields(Order order, OrderPatchRequest request) {
        if (request.getExternalOrderNumber() != null) {
            order.setExternalOrderNumber(request.getExternalOrderNumber());
        }
        if (request.getStatus() != null) {
            order.setOrderStatus(request.getStatus());
        }
        if (request.getFinancialStatus() != null) {
            order.setFinancialStatus(request.getFinancialStatus());
        }
        if (request.getFulfillmentStatus() != null) {
            order.setFulfillmentStatus(request.getFulfillmentStatus());
        }
        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }
        if (request.getOrderTotal() != null) {
            order.setOrderTotalAmount(request.getOrderTotal());
        }
        if (request.getCurrency() != null) {
            order.setCurrency(request.getCurrency());
        }
        if (request.getOrderCreatedAt() != null) {
            order.setOrderCreatedAt(request.getOrderCreatedAt());
        }
        if (request.getOrderUpdatedAt() != null) {
            order.setOrderUpdatedAt(request.getOrderUpdatedAt());
        }
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getOrderId())
                .orgId(order.getTenant().getTenantId())
                .websiteId(order.getStore().getStoreId())
                .externalOrderId(order.getExternalOrderId())
                .externalOrderNumber(order.getExternalOrderNumber())
                .status(order.getOrderStatus())
                .financialStatus(order.getFinancialStatus())
                .fulfillmentStatus(order.getFulfillmentStatus())
                .customerEmail(order.getCustomerEmail())
                .orderTotal(order.getOrderTotalAmount())
                .currency(order.getCurrency())
                .orderCreatedAt(order.getOrderCreatedAt())
                .orderUpdatedAt(order.getOrderUpdatedAt())
                .ingestedAt(order.getIngestedAt())
                .createdAt(Instant.now()) // You may want to add audit fields to entity
                .updatedAt(Instant.now())
                .build();
    }

    private PagedResponse<OrderResponse> mapToPagedResponse(Page<Order> orderPage) {
        return PagedResponse.<OrderResponse>builder()
                .data(orderPage.getContent().stream()
                        .map(this::mapToResponse)
                        .toList())
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .hasNext(orderPage.hasNext())
                .build();
    }
}
