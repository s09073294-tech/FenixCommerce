package com.fenix.commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "store", uniqueConstraints = {
        @UniqueConstraint(name = "uk_store_code_per_tenant", columnNames = { "tenant_id", "store_code" })
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_tenant"))
    private Tenant tenant;

    @Column(name = "store_code", nullable = false, length = 100)
    private String storeCode;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform = Platform.OTHER;

    @Column(name = "timezone", length = 64)
    private String timezone;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreStatus status = StoreStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum Platform {
        SHOPIFY, NETSUITE, CUSTOM, MAGENTO, OTHER
    }

    public enum StoreStatus {
        ACTIVE, INACTIVE
    }
}
