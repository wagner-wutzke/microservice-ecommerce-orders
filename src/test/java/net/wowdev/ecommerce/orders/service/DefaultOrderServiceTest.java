package net.wowdev.ecommerce.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import net.wowdev.ecommerce.orders.TestFixtures;
import net.wowdev.ecommerce.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DefaultOrderServiceTest {
    @Mock private OrderRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;
    private DefaultOrderService service;

    @BeforeEach
    void setUp() {
        service = new DefaultOrderService(repository, eventPublisher);
    }

    @Test
    void findsOrderAndMapsPage() {
        final OrderEntity entity = TestFixtures.orderEntity();
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(entity)));

        assertThat(service.findById(entity.getId()).getOrderNumber()).isEqualTo("ORD-1");
        assertThat(service.findAll(PageRequest.of(0, 10))).hasSize(1);
    }

    @Test
    void throwsWhenOrderIsMissing() {
        final UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(OrderNotFoundException.class);
        assertThatThrownBy(() -> service.update(id, TestFixtures.orderDto())).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void createsAndPublishesCreateEvent() {
        final OrderEntity entity = TestFixtures.orderEntity();
        final OrderDTO order = TestFixtures.orderDto();
        when(repository.save(any(OrderEntity.class))).thenReturn(entity);

        final OrderDTO result = service.create(order);

        assertThat(result.getId()).isEqualTo(entity.getId());
        final ArgumentCaptor<OrderCreatedEvent> event = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().orderDTO())
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    @Test
    void updatesAndPublishesUpdateEvent() {
        final OrderEntity entity = TestFixtures.orderEntity();
        final OrderDTO replacement = TestFixtures.orderDto();
        replacement.setOrderNumber("ORD-2");
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(any(OrderEntity.class))).thenReturn(entity);

        final OrderDTO result = service.update(entity.getId(), replacement);

        assertThat(result).isNotNull();
        final ArgumentCaptor<OrderCreatedEvent> event = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().orderDTO().getId()).isEqualTo(result.getId());
        assertThat(event.getValue().orderDTO())
                .usingRecursiveComparison()
                .isEqualTo(result);
    }

    @Test
    void deletesExistingOrderAndRejectsMissingOrder() {
        final UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);
        service.delete(id);
        verify(repository).deleteById(id);

        when(repository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(OrderNotFoundException.class);
    }
}
