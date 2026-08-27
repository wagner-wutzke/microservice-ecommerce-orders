package net.wowdev.ecommerce.orders.repository;

import net.wowdev.ecommerce.domain.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, UUID> {
}
