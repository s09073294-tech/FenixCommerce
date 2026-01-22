package com.fenix.commerce.service;

import com.fenix.commerce.dto.order.OrderCreateRequest;
import com.fenix.commerce.dto.order.OrderResponse;
import com.fenix.commerce.entity.Order;
import com.fenix.commerce.entity.Store;
import com.fenix.commerce.entity.Tenant;
import com.fenix.commerce.exception.ResourceNotFoundException;
import com.fenix.commerce.repository.OrderRepository;
import com.fenix.commerce.repository.StoreRepository;
import com.fenix.commerce.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID tenantId;
    private UUID storeId;
    private UUID orderId;
    private Tenant tenant;
    private Store store;
    private Order order;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        storeId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        tenant = Tenant.builder()
                .tenantId(tenantId)
                .tenantName("Test Org")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        store = Store.builder()
                .storeId(storeId)
                .tenant(tenant)
                .storeCode("TEST-STORE")
                .storeName("Test Store")
                .platform(Store.Platform.SHOPIFY)
                .status(Store.StoreStatus.ACTIVE)
                .build();

        order = Order.builder()
                .orderId(orderId)
                .tenant(tenant)
                .store(store)
                .externalOrderId("EXT-12345")
                .externalOrderNumber("ORD-001")
                .orderStatus(Order.OrderStatus.CREATED)
                .financialStatus(Order.FinancialStatus.PAID)
                .fulfillmentStatus(Order.FulfillmentOverallStatus.UNFULFILLED)
                .customerEmail("test@example.com")
                .orderTotalAmount(new BigDecimal("99.99"))
                .currency("USD")
                .build();
    }

    @Test
    void createOrder_Success() {
        // Arrange
        OrderCreateRequest request = OrderCreateRequest.builder()
                .orgId(tenantId)
                .websiteId(storeId)
                .externalOrderId("EXT-12345")
                .externalOrderNumber("ORD-001")
                .status(Order.OrderStatus.CREATED)
                .financialStatus(Order.FinancialStatus.PAID)
                .fulfillmentStatus(Order.FulfillmentOverallStatus.UNFULFILLED)
                .customerEmail("test@example.com")
                .orderTotal(new BigDecimal("99.99"))
                .currency("USD")
                .build();

        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(orderRepository.findByTenantTenantIdAndStoreStoreIdAndExternalOrderId(
                tenantId, storeId, "EXT-12345")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals("EXT-12345", response.getExternalOrderId());
        assertEquals(tenantId, response.getOrgId());
        assertEquals(storeId, response.getWebsiteId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_TenantNotFound_ThrowsException() {
        // Arrange
        OrderCreateRequest request = OrderCreateRequest.builder()
                .orgId(tenantId)
                .websiteId(storeId)
                .externalOrderId("EXT-12345")
                .build();

        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderResponse response = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals("EXT-12345", response.getExternalOrderId());
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void deleteOrder_Success() {
        // Arrange
        when(orderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void deleteOrder_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(orderId));
        verify(orderRepository, never()).deleteById(any());
    }
}
