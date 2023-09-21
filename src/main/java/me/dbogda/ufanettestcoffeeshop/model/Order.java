package me.dbogda.ufanettestcoffeeshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import me.dbogda.ufanettestcoffeeshop.enums.ProductType;
import me.dbogda.ufanettestcoffeeshop.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ProductType product;
    private String customer;
    private String employee;
    @Column(name = "time_of_order")
    private LocalDateTime timeOfOrder;
    @Column(name = "time_of_order_issue")
    private LocalDateTime timeOfOrderIssue;
    @Column(name = "time_of_the_last_moving")
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

    @Override
    public String toString() {
        return "Номер заказа: № " + id +
                ", заказано: " + product.getName() +
                ", цена: " + product.getPrice() +
                ", время заказа: " + timeOfOrder.format(DateTimeFormatter.ofPattern("HH:mm")) +
                ", статус заказа: " + status.getName();
    }
}
