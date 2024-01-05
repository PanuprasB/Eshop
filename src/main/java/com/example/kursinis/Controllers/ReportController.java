package com.example.kursinis.Controllers;

import com.example.kursinis.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;

public class ReportController {
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public Button generateReportButton;
    public LineChart ordersChart;
    public Label totalOrdersLabel;
    public Button closeWindowButton;

    private LocalDate startDate;
    private LocalDate endDate;

    private EntityManagerFactory entityManagerFactory;
    private HibernateController hibernateController;

    public void SetData( HibernateController hibernateController) {

        this.hibernateController = hibernateController;
        generateReportButton.setOnAction(e -> generateReport());
        closeWindowButton.setOnAction(e -> closeWindow());

    }

    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<Order> orders = hibernateController.getOrdersCreatedBetween(startDate, endDate);
        totalOrdersLabel.setText("Total orders: " + orders.size());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            long count = orders.stream().filter(order -> order.getDate().equals(finalDate)).count();
            series.getData().add(new XYChart.Data<>(date.toString(), count));
        }
        ordersChart.getData().setAll(series);
    }

    private void closeWindow() {
        closeWindowButton.getScene().getWindow().hide();
    }

}
