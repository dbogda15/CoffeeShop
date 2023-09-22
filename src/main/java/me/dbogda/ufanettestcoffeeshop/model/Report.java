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
@ToString
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    Order order;
    String message;
    @Column(name = "time_of_report")
    LocalDateTime timeOfReport;

    public Report(Order order, String message) {
        this.order = order;
        this.message = message;
    }

    public Report(Order order, String message, LocalDateTime timeOfReport) {
        this.order = order;
        this.message = message;
        this.timeOfReport = timeOfReport;
    }

}
