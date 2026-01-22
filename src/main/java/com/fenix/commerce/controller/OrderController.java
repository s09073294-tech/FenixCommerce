package com.fenix.commerce.controller;

import com.fenix.commerce.dto.PagedResponse;
import com.fenix.commerce.dto.order.OrderCreateRequest;
import com.fenix.commerce.dto.order.OrderPatchRequest;
import com.fenix.commerce.dto.order.OrderResponse;
import com.fenix.commerce.entity.Order;
import com.fenix.commerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create or upsert order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order UUID") @PathVariable UUID orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all orders with optional filters")
    public ResponseEntity<PagedResponse<OrderResponse>> searchOrders(
            @RequestParam(required = false) UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Order.FinancialStatus financialStatus,
            @RequestParam(required = false) Order.FulfillmentOverallStatus fulfillmentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "orderUpdatedAt,desc") String sort) {

        Pageable pageable = createPageable(page, size, sort);

        // If orgId is null, fetch all orders
        PagedResponse<OrderResponse> response;
        if (orgId == null) {
            response = orderService.getAllOrders(pageable); // <- create this method in your service
        } else {
            response = orderService.searchOrders(
                    orgId, websiteId, status, financialStatus, fulfillmentStatus, from, to, pageable);
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    @Operation(summary = "Search orders by external order ID or number")
    public ResponseEntity<PagedResponse<OrderResponse>> searchOrderByExternal(
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID websiteId,
            @RequestParam(required = false) String externalOrderId,
            @RequestParam(required = false) String externalOrderNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<OrderResponse> response = orderService.searchByExternalIds(
                orgId, websiteId, externalOrderId, externalOrderNumber, pageable);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}")
    @Operation(summary = "Update order (full replace)")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order (partial)")
    public ResponseEntity<OrderResponse> patchOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderPatchRequest request) {
        OrderResponse response = orderService.patchOrder(orderId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
