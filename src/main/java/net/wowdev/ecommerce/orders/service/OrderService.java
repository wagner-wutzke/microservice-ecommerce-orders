package net.wowdev.ecommerce.orders.service;

import net.wowdev.ecommerce.domain.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderDTO findById(UUID id);

    Page<OrderDTO> findAll(Pageable pageable);

    OrderDTO create(OrderDTO order);

    OrderDTO update(UUID id, OrderDTO order);

    void delete(UUID id);
}
