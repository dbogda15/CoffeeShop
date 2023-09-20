package me.dbogda.ufanettestcoffeeshop.service;

import me.dbogda.ufanettestcoffeeshop.model.Report;

import java.util.List;

public interface ReportService {
    Report create(Report report);
    List<Report> getReportsByOrderId(Long id);
    List<Report> getAll();


}
