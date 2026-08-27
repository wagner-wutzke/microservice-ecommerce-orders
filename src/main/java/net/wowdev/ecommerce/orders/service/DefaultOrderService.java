package net.wowdev.ecommerce.orders.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import net.wowdev.ecommerce.domain.mapper.OrderMapper;
import net.wowdev.ecommerce.orders.repository.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

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
        if(orderDTO.getId() == null) { orderDTO.setId(UUID.randomUUID()); }
        OrderEntity saved = orderRepository.save(OrderMapper.toEntity(orderDTO));

        OrderDTO result = OrderMapper.toDto(saved);
        eventPublisher.publishEvent(new OrderCreatedEvent(
                orderDTO.getId(),
                orderDTO,
                LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        return result;
    }

    @Override
    @Transactional
    public OrderDTO update(final UUID id, final OrderDTO order) {
        OrderEntity current = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        OrderDTO replacement = OrderMapper.toDto(current);
        replacement.setOrderNumber(order.getOrderNumber());
        replacement.setStatus(order.getStatus());
        replacement.setTotalAmount(order.getTotalAmount());
        replacement.setShippingAmount(order.getShippingAmount());
        replacement.setShippingAmount(order.getShippingAmount());
        replacement.setOrderAmount(order.getOrderAmount());
        replacement.setTaxAmount(order.getTaxAmount());
        OrderDTO result = OrderMapper.toDto(orderRepository.save(OrderMapper.toEntity(replacement)));
        eventPublisher.publishEvent(new OrderCreatedEvent(
                result.getId(),
                result,
                LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        return result;
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
