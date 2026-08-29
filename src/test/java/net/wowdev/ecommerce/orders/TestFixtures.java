package net.wowdev.ecommerce.orders;

import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.enums.OrderStatus;
import net.wowdev.ecommerce.domain.mapper.OrderMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class TestFixtures {
    private TestFixtures() {
    }

    public static OrderDTO orderDto() {
        final OrderDTO order = new OrderDTO();
        order.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        order.setCustomerId(UUID.fromString("22222222-1111-1111-1111-222222222222"));
        order.setOrderNumber("ORD-1");
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("10.00"));
        order.setDiscountAmount(new BigDecimal("8.00"));
        order.setShippingAmount(new BigDecimal("1.00"));
        order.setOrderAmount(new BigDecimal("9.00"));
        order.setTaxAmount(new BigDecimal("1.00"));
        order.setOrderLines(List.of());
        order.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        order.setModifiedAt(Instant.parse("2026-01-02T00:00:00Z"));
        return order;
    }

    public static OrderEntity orderEntity() {
        return OrderMapper.toEntity(orderDto());
    }
}
