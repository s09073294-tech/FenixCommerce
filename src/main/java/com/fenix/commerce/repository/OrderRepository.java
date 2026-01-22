package com.fenix.commerce.repository;

import com.fenix.commerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByTenantTenantIdAndStoreStoreIdAndExternalOrderId(
            UUID tenantId, UUID storeId, String externalOrderId);

    Page<Order> findByTenantTenantId(UUID tenantId, Pageable pageable);

    Page<Order> findByTenantTenantIdAndStoreStoreId(UUID tenantId, UUID storeId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId " +
            "AND (:storeId IS NULL OR o.store.storeId = :storeId) " +
            "AND (:status IS NULL OR o.orderStatus = :status) " +
            "AND (:financialStatus IS NULL OR o.financialStatus = :financialStatus) " +
            "AND (:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus) " +
            "AND (:from IS NULL OR o.orderUpdatedAt >= :from) " +
            "AND (:to IS NULL OR o.orderUpdatedAt <= :to)")
    Page<Order> searchOrders(
            @Param("tenantId") UUID tenantId,
            @Param("storeId") UUID storeId,
            @Param("status") Order.OrderStatus status,
            @Param("financialStatus") Order.FinancialStatus financialStatus,
            @Param("fulfillmentStatus") Order.FulfillmentOverallStatus fulfillmentStatus,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.tenant.tenantId = :tenantId " +
            "AND (:storeId IS NULL OR o.store.storeId = :storeId) " +
            "AND (:externalOrderId IS NULL OR o.externalOrderId = :externalOrderId) " +
            "AND (:externalOrderNumber IS NULL OR o.externalOrderNumber = :externalOrderNumber)")
    Page<Order> searchByExternalIds(
            @Param("tenantId") UUID tenantId,
            @Param("storeId") UUID storeId,
            @Param("externalOrderId") String externalOrderId,
            @Param("externalOrderNumber") String externalOrderNumber,
            Pageable pageable);

    boolean existsByTenantTenantIdAndStoreStoreIdAndExternalOrderId(
            UUID tenantId, UUID storeId, String externalOrderId);
}
