package net.wowdev.ecommerce.orders.repository;

import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.orders.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findsPersistedOrder() {
        final var order = TestFixtures.orderEntity();
        jdbcTemplate.update("""
                                    insert into orders (
                                        id, transaction_id, customer_id, order_status,
                                        total_amount, shipping_amount, tax_amount, discount_amount, order_amount,
                                        order_number, created_at, modified_at
                                    ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    """,
                            order.getId(),
                            order.getTransactionId(),
                            order.getCustomerId(),
                            order.getOrderStatus().name(),
                            order.getTotalAmount(),
                            order.getShippingAmount(),
                            order.getTaxAmount(),
                            order.getDiscountAmount(),
                            order.getOrderAmount(),
                            order.getOrderNumber(),
                            Timestamp.from(order.getCreatedAt()),
                            Timestamp.from(order.getModifiedAt()));

        assertThat(repository.findById(order.getId()))
                .get()
                .extracting(OrderEntity::getOrderNumber, OrderEntity::getOrderStatus)
                .containsExactly(order.getOrderNumber(), order.getOrderStatus());
        assertThat(repository.findAll()).hasSize(1);
    }
}
