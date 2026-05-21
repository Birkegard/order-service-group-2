package se.iths.christoffer.orderservicegroup2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.christoffer.orderservicegroup2.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
