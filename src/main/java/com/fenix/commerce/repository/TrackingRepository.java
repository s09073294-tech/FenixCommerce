package com.fenix.commerce.repository;

import com.fenix.commerce.entity.Tracking;
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
public interface TrackingRepository extends JpaRepository<Tracking, UUID> {

    Optional<Tracking> findByTenantTenantIdAndTrackingNumber(UUID tenantId, String trackingNumber);

    Page<Tracking> findByFulfillmentFulfillmentId(UUID fulfillmentId, Pageable pageable);

    @Query("SELECT t FROM Tracking t WHERE t.fulfillment.fulfillmentId = :fulfillmentId " +
            "AND (:status IS NULL OR t.trackingStatus = :status) " +
            "AND (:carrier IS NULL OR t.carrier = :carrier) " +
            "AND (:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) " +
            "AND (:from IS NULL OR t.updatedAt >= :from) " +
            "AND (:to IS NULL OR t.updatedAt <= :to)")
    Page<Tracking> searchTracking(
            @Param("fulfillmentId") UUID fulfillmentId,
            @Param("status") Tracking.TrackingStatus status,
            @Param("carrier") String carrier,
            @Param("trackingNumber") String trackingNumber,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    boolean existsByTenantTenantIdAndTrackingNumber(UUID tenantId, String trackingNumber);
}
