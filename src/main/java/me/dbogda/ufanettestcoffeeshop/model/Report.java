package me.dbogda.ufanettestcoffeeshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "order_id")
    Order order;
    String message;
    LocalDateTime localDateTime;

    public Report(Order order, String message) {
        this.order = order;
        this.message = message;
    }

    public Report(Order order, String message, LocalDateTime localDateTime) {
        this.order = order;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "Отчёт № " + id +
                ", по заказу № " + order.getId() +
                ". Комментарий: " + message + '\'' +
                ", дата отчета: " + localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
