package com.fenix.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "tracking", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tracking_number", columnNames = { "tenant_id", "tracking_number" })
}, indexes = {
        @Index(name = "idx_tracking_tenant_fulfillment", columnList = "tenant_id, fulfillment_id"),
        @Index(name = "idx_tracking_tenant_status", columnList = "tenant_id, tracking_status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tracking {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "tracking_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tracking_tenant"))
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfillment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tracking_fulfillment"))
    private Fulfillment fulfillment;

    @Column(name = "tracking_number", nullable = false, length = 128)
    private String trackingNumber;

    @Column(name = "tracking_url", length = 1024)
    private String trackingUrl;

    @Column(name = "carrier", length = 64)
    private String carrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_status", nullable = false)
    private TrackingStatus trackingStatus = TrackingStatus.UNKNOWN;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "last_event_at")
    private LocalDateTime lastEventAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum TrackingStatus {
        LABEL_CREATED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, EXCEPTION, UNKNOWN
    }
}
