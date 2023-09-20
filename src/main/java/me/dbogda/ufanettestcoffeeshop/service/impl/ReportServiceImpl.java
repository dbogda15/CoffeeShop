package me.dbogda.ufanettestcoffeeshop.service.impl;

import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.model.Order;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.repository.OrderRepository;
import me.dbogda.ufanettestcoffeeshop.repository.ReportRepository;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final OrderRepository orderRepository;

    @Override
    public Report create(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> getReportsByOrderId(Long id) {
        Optional<Order> optional = orderRepository.findById(id);
        if(optional.isEmpty()){
            throw new NotFoundException("Отчёт с id = " + id + " не существует!");
        } else {
            return reportRepository.getAllByOrder(optional.get());
        }
    }
}
