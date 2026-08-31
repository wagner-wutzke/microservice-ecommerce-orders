package net.wowdev.ecommerce.orders.repository;

import java.util.UUID;
import net.wowdev.ecommerce.domain.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, UUID> {}
