package org.project.sales.analysis;

import org.project.sales.model.SalesRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides analytical operations over a collection of SalesRecord objects.
 * All operations are implemented using Java Streams and lambda expressions.
 */
public class SalesDataAnalyzer {

    /**
     * Load sales records from a CSV file.
     * Skips the header line and converts each line into a SalesRecord.
     */
    public List<SalesRecord> loadFromCsv(Path csvPath) throws IOException {
        try (Stream<String> lines = Files.lines(csvPath)) {
            return lines
                    .skip(1) // skip header
                    .filter(line -> !line.isBlank())
                    .map(SalesRecord::fromCsvLine)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Total revenue across all records.
     */
    public double calculateTotalRevenue(List<SalesRecord> records) {
        return records.stream()
                .mapToDouble(SalesRecord::getRevenue)
                .sum();
    }

    /**
     * Revenue grouped by region.
     */
    public Map<String, Double> calculateRevenueByRegion(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.summingDouble(SalesRecord::getRevenue)
                ));
    }

    /**
     * Total units sold per product.
     */
    public Map<String, Integer> calculateUnitsSoldByProduct(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getProduct,
                        Collectors.summingInt(SalesRecord::getQuantity)
                ));
    }

    /**
     * Revenue grouped by month of the order date.
     */
    public Map<Month, Double> calculateMonthlyRevenue(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getOrderDate().getMonth(),
                        Collectors.summingDouble(SalesRecord::getRevenue)
                ));
    }

    /**
     * Number of orders per category.
     */
    public Map<String, Long> countOrdersByCategory(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.counting()
                ));
    }

    /**
     * Average order revenue per customer.
     */
    public Map<String, Double> calculateAverageOrderValuePerCustomer(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCustomerId,
                        Collectors.averagingDouble(SalesRecord::getRevenue)
                ));
    }

    /**
     * Find the order with the highest revenue.
     */
    public Optional<SalesRecord> findLargestOrderByRevenue(List<SalesRecord> records) {
        return records.stream()
                .max(Comparator.comparingDouble(SalesRecord::getRevenue));
    }

    /**
     * Basic summary statistics (count, min, max, avg, sum) for revenue.
     */
    public DoubleSummaryStatistics revenueSummary(List<SalesRecord> records) {
        return records.stream()
                .mapToDouble(SalesRecord::getRevenue)
                .summaryStatistics();
    }
}
