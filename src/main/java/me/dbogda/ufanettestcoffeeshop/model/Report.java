package me.dbogda.ufanettestcoffeeshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(order, report.order) && Objects.equals(message, report.message) && Objects.equals(localDateTime, report.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, message, localDateTime);
    }

    @Override
    public String toString() {
        return "Отчёт № " + id +
                ", по заказу № " + order.getId() +
                ". Комментарий: " + message + '\'' +
                ", дата отчета: " + localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
