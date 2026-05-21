package se.iths.christoffer.orderservicegroup2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.christoffer.orderservicegroup2.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
}
