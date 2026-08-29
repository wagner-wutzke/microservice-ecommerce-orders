package net.wowdev.ecommerce.orders.controller;

import jakarta.validation.Valid;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.orders.service.OrderNotFoundException;
import net.wowdev.ecommerce.orders.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(final OrderService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public OrderDTO get(@PathVariable final UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public Page<OrderDTO> list(@RequestParam(defaultValue = "0") final int page,
                               @RequestParam(defaultValue = "20") final int pageSize) {
        if (page < 0 || pageSize < 1 || pageSize > 100) throw new IllegalArgumentException("Invalid pagination");
        return service.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody final OrderDTO order) {
        OrderDTO created = service.create(order);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public OrderDTO update(@PathVariable final UUID id, @Valid @RequestBody final OrderDTO order) {
        return service.update(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> notFound(final OrderNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(final IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
