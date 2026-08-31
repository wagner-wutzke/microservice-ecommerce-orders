package net.wowdev.ecommerce.orders.repository;

import java.util.UUID;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {}
