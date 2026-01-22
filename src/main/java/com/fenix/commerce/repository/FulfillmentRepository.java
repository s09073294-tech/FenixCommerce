package com.fenix.commerce.repository;

import com.fenix.commerce.entity.Fulfillment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {

    Optional<Fulfillment> findByTenantTenantIdAndOrderOrderIdAndExternalFulfillmentId(
            UUID tenantId, UUID orderId, String externalFulfillmentId);

    Page<Fulfillment> findByOrderOrderId(UUID orderId, Pageable pageable);

    Page<Fulfillment> findByTenantTenantIdAndOrderOrderId(UUID tenantId, UUID orderId, Pageable pageable);

    @Query("SELECT f FROM Fulfillment f WHERE f.order.orderId = :orderId " +
            "AND (:status IS NULL OR f.fulfillmentStatus = :status) " +
            "AND (:carrier IS NULL OR f.carrier = :carrier) " +
            "AND (:from IS NULL OR f.updatedAt >= :from) " +
            "AND (:to IS NULL OR f.updatedAt <= :to)")
    Page<Fulfillment> searchFulfillments(
            @Param("orderId") UUID orderId,
            @Param("status") Fulfillment.FulfillmentStatus status,
            @Param("carrier") String carrier,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query("SELECT f FROM Fulfillment f WHERE f.order.orderId = :orderId " +
            "AND f.externalFulfillmentId = :externalFulfillmentId")
    Page<Fulfillment> searchByExternalId(
            @Param("orderId") UUID orderId,
            @Param("externalFulfillmentId") String externalFulfillmentId,
            Pageable pageable);

    boolean existsByTenantTenantIdAndOrderOrderIdAndExternalFulfillmentId(
            UUID tenantId, UUID orderId, String externalFulfillmentId);
}
