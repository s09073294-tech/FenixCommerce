package com.fenix.commerce.repository;

import com.fenix.commerce.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByTenantName(String tenantName);

    Page<Tenant> findByStatus(Tenant.TenantStatus status, Pageable pageable);

    Page<Tenant> findByTenantNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByTenantName(String tenantName);
}
