package com.fenix.commerce.controller;

import com.fenix.commerce.dto.PagedResponse;
import com.fenix.commerce.dto.fulfillment.FulfillmentCreateRequest;
import com.fenix.commerce.dto.fulfillment.FulfillmentPatchRequest;
import com.fenix.commerce.dto.fulfillment.FulfillmentResponse;
import com.fenix.commerce.entity.Fulfillment;
import com.fenix.commerce.service.FulfillmentService;
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

import java.time.Instant;
import java.util.UUID;


@RestController
@RequestMapping("/orders/{orderId}/fulfillments")
@RequiredArgsConstructor
@Tag(name = "Fulfillments", description = "Fulfillment management APIs")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    @PostMapping
    @Operation(summary = "Create fulfillment for an order")
    public ResponseEntity<FulfillmentResponse> createFulfillment(
            @Parameter(description = "Order UUID") @PathVariable UUID orderId,
            @Valid @RequestBody FulfillmentCreateRequest request) {
        FulfillmentResponse response = fulfillmentService.createFulfillment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{fulfillmentId}")
    @Operation(summary = "Get fulfillment by ID")
    public ResponseEntity<FulfillmentResponse> getFulfillmentById(
            @PathVariable UUID orderId,
            @Parameter(description = "Fulfillment UUID") @PathVariable UUID fulfillmentId) {
        FulfillmentResponse response = fulfillmentService.getFulfillmentById(orderId, fulfillmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List/search fulfillments for an order")
    public ResponseEntity<PagedResponse<FulfillmentResponse>> listFulfillments(
            @PathVariable UUID orderId,
            @RequestParam(required = false) Fulfillment.FulfillmentStatus status,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort) {

        Pageable pageable = createPageable(page, size, sort);
        PagedResponse<FulfillmentResponse> response = fulfillmentService.listFulfillments(
                orderId, status, carrier, from, to, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search fulfillment by external fulfillment ID")
    public ResponseEntity<PagedResponse<FulfillmentResponse>> searchFulfillmentsByExternal(
            @PathVariable UUID orderId,
            @RequestParam String externalFulfillmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<FulfillmentResponse> response = fulfillmentService.searchByExternalId(
                orderId, externalFulfillmentId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fulfillmentId}")
    @Operation(summary = "Update fulfillment (full replace)")
    public ResponseEntity<FulfillmentResponse> updateFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @Valid @RequestBody FulfillmentCreateRequest request) {
        FulfillmentResponse response = fulfillmentService.updateFulfillment(
                orderId, fulfillmentId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{fulfillmentId}")
    @Operation(summary = "Update fulfillment (partial)")
    public ResponseEntity<FulfillmentResponse> patchFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId,
            @Valid @RequestBody FulfillmentPatchRequest request) {
        FulfillmentResponse response = fulfillmentService.patchFulfillment(
                orderId, fulfillmentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fulfillmentId}")
    @Operation(summary = "Delete fulfillment")
    public ResponseEntity<Void> deleteFulfillment(
            @PathVariable UUID orderId,
            @PathVariable UUID fulfillmentId) {
        fulfillmentService.deleteFulfillment(orderId, fulfillmentId);
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
