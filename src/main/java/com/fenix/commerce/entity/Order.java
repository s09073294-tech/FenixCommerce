package com.fenix.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "orders", uniqueConstraints = {
        @UniqueConstraint(name = "uk_order_external", columnNames = { "tenant_id", "store_id", "external_order_id" })
}, indexes = {
        @Index(name = "idx_orders_tenant_updated", columnList = "tenant_id, order_updated_at"),
        @Index(name = "idx_orders_store_updated", columnList = "store_id, order_updated_at"),
        @Index(name = "idx_orders_tenant_number", columnList = "tenant_id, external_order_number")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orders_tenant"))
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orders_store"))
    private Store store;

    @Column(name = "external_order_id", nullable = false, length = 128)
    private String externalOrderId;

    @Column(name = "external_order_number", length = 128)
    private String externalOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status", nullable = false)
    private FinancialStatus financialStatus = FinancialStatus.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    private FulfillmentOverallStatus fulfillmentStatus = FulfillmentOverallStatus.UNKNOWN;

    @Column(name = "customer_email", length = 320)
    private String customerEmail;

    @Column(name = "order_total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderTotalAmount = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "order_created_at")
    private LocalDateTime orderCreatedAt;

    @Column(name = "order_updated_at")
    private LocalDateTime orderUpdatedAt;

    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload_json", columnDefinition = "JSON")
    private String rawPayloadJson;

    @PrePersist
    protected void onCreate() {
        if (ingestedAt == null) {
            ingestedAt = Instant.now();
        }
    }

    public enum OrderStatus {
        CREATED, CANCELLED, CLOSED
    }

    public enum FinancialStatus {
        UNKNOWN, PENDING, PAID, PARTIALLY_PAID, REFUNDED, PARTIALLY_REFUNDED, VOIDED
    }

    public enum FulfillmentOverallStatus {
        UNFULFILLED, PARTIAL, FULFILLED, CANCELLED, UNKNOWN
    }
}
