package com.fenix.commerce.repository;

import com.fenix.commerce.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByTenantTenantIdAndStoreCode(UUID tenantId, String storeCode);

    Page<Store> findByTenantTenantId(UUID tenantId, Pageable pageable);

    Page<Store> findByTenantTenantIdAndStatus(UUID tenantId, Store.StoreStatus status, Pageable pageable);

    Page<Store> findByTenantTenantIdAndPlatform(UUID tenantId, Store.Platform platform, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.tenant.tenantId = :tenantId " +
            "AND (:code IS NULL OR s.storeCode LIKE %:code%) " +
            "AND (:status IS NULL OR s.status = :status) " +
            "AND (:platform IS NULL OR s.platform = :platform)")
    Page<Store> searchStores(@Param("tenantId") UUID tenantId,
            @Param("code") String code,
            @Param("status") Store.StoreStatus status,
            @Param("platform") Store.Platform platform,
            Pageable pageable);

    boolean existsByTenantTenantIdAndStoreCode(UUID tenantId, String storeCode);
}
