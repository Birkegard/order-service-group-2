package se.iths.christoffer.orderservicegroup2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate orderDate;
    private String customerName;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    private int totalPrice;

}
