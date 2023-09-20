package me.dbogda.ufanettestcoffeeshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "order")
    private List<Report> reports;

    public Order(ProductType product, String customer) {
        this.product = product;
        this.customer = customer;
    }
    public Order(Long id, ProductType product, String customer, Status status, LocalDateTime timeOfOrder) {
        this.id = id;
        this.product = product;
        this.customer = customer;
        this.status = status;
        this.timeOfOrder = timeOfOrder;
    }
    public Order(ProductType product, String customer, LocalDateTime timeOfOrder, LocalDateTime timeOfOrderIssue, LocalDateTime timeOfTheLastMoving, Status status) {
        this.product = product;
        this.customer = customer;
        this.timeOfOrder = timeOfOrder;
        this.timeOfOrderIssue = timeOfOrderIssue;
        this.timeOfTheLastMoving = timeOfTheLastMoving;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public LocalDateTime getTimeOfOrder() {
        return timeOfOrder;
    }

    public void setTimeOfOrder(LocalDateTime timeOfOrder) {
        this.timeOfOrder = timeOfOrder;
    }

    public LocalDateTime getTimeOfOrderIssue() {
        return timeOfOrderIssue;
    }

    public void setTimeOfOrderIssue(LocalDateTime timeOfOrderIssue) {
        this.timeOfOrderIssue = timeOfOrderIssue;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getTimeOfTheLastMoving() {
        return timeOfTheLastMoving;
    }

    public void setTimeOfTheLastMoving(LocalDateTime timeOfTheLastMoving) {
        this.timeOfTheLastMoving = timeOfTheLastMoving;
    }

    public ProductType getProduct() {
        return product;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && product == order.product && Objects.equals(customer, order.customer) && Objects.equals(employee, order.employee) && Objects.equals(timeOfOrder, order.timeOfOrder) && Objects.equals(timeOfOrderIssue, order.timeOfOrderIssue) && Objects.equals(timeOfTheLastMoving, order.timeOfTheLastMoving) && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, customer, employee, timeOfOrder, timeOfOrderIssue, timeOfTheLastMoving, status);
    }

    @Override
    public String toString() {
        return "Номер заказа: № " + id +
                ", заказано: " + product.getName() +
                ", цена: " + product.getPrice() +
                ", время заказа: " + timeOfOrder.format(DateTimeFormatter.ofPattern("HH:mm")) +
                //", примерное время выдачи заказа: " + timeOfOrderIssue.format(DateTimeFormatter.ofPattern("HH:mm")) +
                ", статус заказа: " + status.getName();
    }
}
