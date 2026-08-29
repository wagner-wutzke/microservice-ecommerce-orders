package net.wowdev.ecommerce.orders.mapper;

import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.OrderEntity;
import net.wowdev.ecommerce.domain.mapper.OrderMapper;
import net.wowdev.ecommerce.orders.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {
    @Test
    void mapsOrderDtoToEntityAndBack() {
        final OrderDTO source = TestFixtures.orderDto();

        final OrderEntity entity = OrderMapper.toEntity(source);
        final OrderDTO result = OrderMapper.toDto(entity);

        assertThat(entity.getId()).isEqualTo(source.getId());
        assertThat(entity.getOrderNumber()).isEqualTo(source.getOrderNumber());
        assertThat(result).isEqualTo(source);
    }

    @Test
    void mapsNullValuesWithoutFailure() {
        assertThat(OrderMapper.toEntity(null)).isNull();
        assertThat(OrderMapper.toDto(null)).isNull();
    }
}
