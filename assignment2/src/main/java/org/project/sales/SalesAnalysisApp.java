package org.project.sales;

import org.project.sales.analysis.SalesDataAnalyzer;
import org.project.sales.model.SalesRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class SalesAnalysisApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar assignment2.jar <path-to-sales_data.csv>");
            System.exit(1);
        }

        Path csvPath = Path.of(args[0]);
        SalesDataAnalyzer analyzer = new SalesDataAnalyzer();

        try {
            List<SalesRecord> records = analyzer.loadFromCsv(csvPath);

            System.out.println("Loaded " + records.size() + " records from " + csvPath);
            System.out.println("====================================================");

            // 1. Total revenue
            double totalRevenue = analyzer.calculateTotalRevenue(records);
            System.out.printf("Total revenue: %.2f%n", totalRevenue);

            // 2. Revenue by region
            System.out.println("\nRevenue by region:");
            analyzer.calculateRevenueByRegion(records)
                    .forEach((region, revenue) ->
                            System.out.printf("  %s: %.2f%n", region, revenue));

            // 3. Units sold by product
            System.out.println("\nUnits sold by product:");
            analyzer.calculateUnitsSoldByProduct(records)
                    .forEach((product, units) ->
                            System.out.printf("  %s: %d%n", product, units));

            // 4. Monthly revenue
            System.out.println("\nMonthly revenue:");
            Map<Month, Double> monthlyRevenue = analyzer.calculateMonthlyRevenue(records);
            monthlyRevenue.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // sort by month
                    .forEach(entry ->
                            System.out.printf("  %s: %.2f%n",
                                    entry.getKey(), entry.getValue()));

            // 5. Orders per category
            System.out.println("\nOrder count by category:");
            analyzer.countOrdersByCategory(records)
                    .forEach((category, count) ->
                            System.out.printf("  %s: %d%n", category, count));

            // 6. Average order value per customer
            System.out.println("\nAverage order value per customer:");
            analyzer.calculateAverageOrderValuePerCustomer(records)
                    .forEach((customerId, avg) ->
                            System.out.printf("  %s: %.2f%n", customerId, avg));

            // 7. Largest order
            analyzer.findLargestOrderByRevenue(records)
                    .ifPresent(largest -> {
                        System.out.println("\nLargest order by revenue:");
                        System.out.printf("  Order ID: %s, Customer: %s, Revenue: %.2f%n",
                                largest.getOrderId(),
                                largest.getCustomerId(),
                                largest.getRevenue());
                    });

            // 8. Revenue summary
            var summary = analyzer.revenueSummary(records);
            System.out.println("\nRevenue summary statistics:");
            System.out.printf("  Count: %d%n", summary.getCount());
            System.out.printf("  Min: %.2f%n", summary.getMin());
            System.out.printf("  Max: %.2f%n", summary.getMax());
            System.out.printf("  Avg: %.2f%n", summary.getAverage());
            System.out.printf("  Sum: %.2f%n", summary.getSum());

        } catch (IOException e) {
            System.err.println("Failed to read CSV file: " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }
}
