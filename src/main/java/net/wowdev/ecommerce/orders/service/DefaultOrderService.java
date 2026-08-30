package net.wowdev.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import net.wowdev.ecommerce.domain.mapper.OrderMapper;
import net.wowdev.ecommerce.orders.messaging.OrderProducer;
import net.wowdev.ecommerce.orders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById(final UUID id) {
        return orderRepository.findById(id).map(OrderMapper::toDto)
                              .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(final Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDTO create(final OrderDTO orderDTO) {
        if (orderDTO.getId() == null) {
            orderDTO.setId(UUID.randomUUID());
        }
        OrderEntity saved = orderRepository.save(OrderMapper.toEntity(orderDTO));

        OrderDTO savedOrderDTO = OrderMapper.toDto(saved);

        OrderProcessingStartedEvent orderCreatedEvent = new OrderProcessingStartedEvent(
                UUID.randomUUID(),
                "TX_" + savedOrderDTO.getId(),
                orderDTO,
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
        orderProducer.publishOrderProcessingStartedEvent(orderCreatedEvent);
        return savedOrderDTO;
    }

    @Override
    @Transactional
    public OrderDTO update(final UUID id, final OrderDTO order) {
        OrderEntity current = orderRepository.findById(id)
                                             .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        OrderDTO replacement = OrderMapper.toDto(current);
        replacement.setOrderNumber(order.getOrderNumber());
        replacement.setOrderStatus(order.getOrderStatus());
        replacement.setTotalAmount(order.getTotalAmount());
        replacement.setShippingAmount(order.getShippingAmount());
        replacement.setOrderLines(order.getOrderLines());
        replacement.setOrderAmount(order.getOrderAmount());
        replacement.setTaxAmount(order.getTaxAmount());
        replacement.setDiscountAmount(order.getDiscountAmount());
        return OrderMapper.toDto(orderRepository.save(OrderMapper.toEntity(replacement)));
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

}
