package me.dbogda.ufanettestcoffeeshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import me.dbogda.ufanettestcoffeeshop.model.Report;
import me.dbogda.ufanettestcoffeeshop.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/reports")
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
        @ApiResponse(responseCode = "400", description = "Ошибка в параметрах запроса"),
        @ApiResponse(responseCode = "404", description = "Несуществующий URL"),
        @ApiResponse(responseCode = "500", description = "Ошибка со стороны сервера")
})
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/id")
    @Operation(
            summary = "Получить все отчеты по заказу",
            description = "Введите ID заказа")
    public ResponseEntity<List<Report>> getReportsByOrderId(@RequestParam Long orderId){
        List<Report> result = reportService.getReportsByOrderId(orderId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все отчеты за всё время")
    public ResponseEntity<List<Report>> getAll(){
        List<Report> result = reportService.getAll();
        return ResponseEntity.ok(result);
    }
}
