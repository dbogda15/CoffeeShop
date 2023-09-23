package me.dbogda.ufanettestcoffeeshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ProductType product;
    private String customer;
    private String employee;
    private LocalDateTime timeOfOrder;
    private LocalDateTime timeOfOrderIssue;
    private LocalDateTime timeOfTheLastMoving;
    private Status status;

    @JsonIgnoreProperties(value = "order", allowGetters = true)
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private List<Report> reports = new ArrayList<>();

    public Order(ProductType product, String customer) {
        this.product = product;
        this.customer = customer;
    }
}
