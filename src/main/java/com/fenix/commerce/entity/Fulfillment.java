package com.fenix.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "fulfillments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fulfillment_external", columnNames = { "tenant_id", "order_id",
                "external_fulfillment_id" })
}, indexes = {
        @Index(name = "idx_fulfillments_tenant_order", columnList = "tenant_id, order_id"),
        @Index(name = "idx_fulfillments_tenant_updated", columnList = "tenant_id, updated_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fulfillment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "fulfillment_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID fulfillmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fulfillments_tenant"))
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fulfillments_order"))
    private Order order;

    @Column(name = "external_fulfillment_id", nullable = false, length = 128)
    private String externalFulfillmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.UNKNOWN;

    @Column(name = "carrier", length = 64)
    private String carrier;

    @Column(name = "service_level", length = 64)
    private String serviceLevel;

    @Column(name = "ship_from_location")
    private String shipFromLocation;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload_json", columnDefinition = "JSON")
    private String rawPayloadJson;

    public enum FulfillmentStatus {
        CREATED, SHIPPED, DELIVERED, CANCELLED, FAILED, UNKNOWN
    }
}
