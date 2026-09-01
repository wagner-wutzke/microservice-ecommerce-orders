package net.wowdev.ecommerce.orders.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.enums.OrderStatus;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import net.wowdev.ecommerce.domain.mapper.OrderMapper;
import net.wowdev.ecommerce.orders.messaging.OrderProducer;
import net.wowdev.ecommerce.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderProducer orderProducer;

  @Value("${business.config.vat-rate}")
  private Double vatRate;

  @Value("${business.config.shipping-cost}")
  private Double shippingCost;

  @Override
  @Transactional(readOnly = true)
  public OrderDTO findById(final UUID id) {
    return orderRepository
        .findById(id)
        .map(OrderMapper::toDto)
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
    log.debug(">>>> Creating new Order record...");
    if (orderDTO.getId() == null) {
      final UUID orderId = UUID.randomUUID();
      orderDTO.setId(orderId);
      orderDTO.setOrderStatus(OrderStatus.CREATED);
      orderDTO
          .getOrderLines()
          .forEach(
              orderLine -> {
                UUID orderLineId = UUID.randomUUID();
                orderLine.setOrderId(orderId);
                orderLine.setId(orderLineId);
              });
    }
    calculateOrderAmounts(orderDTO);

    final OrderEntity savedOrderEntity = orderRepository.save(OrderMapper.toEntity(orderDTO));
    log.debug(">>>> Created new Order {}", savedOrderEntity);
    final OrderDTO savedOrderDTO = OrderMapper.toDto(savedOrderEntity);

    OrderProcessingStartedEvent orderProcessingStartedEvent =
        new OrderProcessingStartedEvent(
            UUID.randomUUID(),
            savedOrderDTO.getId().toString(),
            savedOrderDTO,
            LocalDateTime.now().toInstant(ZoneOffset.UTC));
    // TODO: persist event before publishing (outbox pattern)
    orderProducer.publish(orderProcessingStartedEvent);
    return savedOrderDTO;
  }

  protected void calculateOrderAmounts(OrderDTO orderDTO) {
    BigDecimal orderAmount =
        orderDTO.getOrderLines().stream()
            .map(line -> line.getPrice().multiply(BigDecimal.valueOf(line.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    orderDTO.setOrderAmount(orderAmount);
    orderDTO.setShippingAmount(BigDecimal.valueOf(shippingCost));
    orderDTO.setDiscountAmount(BigDecimal.ZERO); // TODO implement discount table
    orderDTO.setTaxAmount(BigDecimal.valueOf(vatRate).multiply(orderAmount));
    orderDTO.setTotalAmount(
        orderAmount
            .add(orderDTO.getDiscountAmount())
            .add(orderDTO.getTaxAmount())
            .add(orderDTO.getShippingAmount())
            .add(orderDTO.getDiscountAmount()));
  }

  @Override
  @Transactional
  public OrderDTO update(final UUID id, final OrderDTO order) {
    OrderEntity current =
        orderRepository
            .findById(id)
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
