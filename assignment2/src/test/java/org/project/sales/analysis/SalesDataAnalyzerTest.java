package org.project.sales.analysis;

import org.junit.jupiter.api.Test;
import org.project.sales.model.SalesRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SalesDataAnalyzerTest {

    private final SalesDataAnalyzer analyzer = new SalesDataAnalyzer();

    /**
     * Create a small in-memory dataset for testing.
     * (Deliberately similar to the CSV example but smaller.)
     */
    private List<SalesRecord> createSampleRecords() {
        return List.of(
                new SalesRecord("1001", LocalDate.of(2024, 1, 5), "North",
                        "Wireless Mouse", "Accessories", 3, 25.50, "C001"),
                new SalesRecord("1002", LocalDate.of(2024, 1, 6), "South",
                        "Mechanical Keyboard", "Accessories", 2, 80.00, "C002"),
                new SalesRecord("1003", LocalDate.of(2024, 2, 1), "North",
                        "27\" Monitor", "Electronics", 1, 220.00, "C001"),
                new SalesRecord("1004", LocalDate.of(2024, 2, 10), "West",
                        "Gaming Laptop", "Electronics", 1, 1500.00, "C003")
        );
    }

    @Test
    void loadFromCsv_readsAllRecords() throws IOException {
        // Arrange: create a temporary CSV file
        String csvContent = String.join("\n",
                "order_id,order_date,region,product,category,quantity,unit_price,customer_id",
                "2001,2024-01-01,North,Test Mouse,Accessories,2,10.00,C001",
                "2002,2024-01-02,South,Test Keyboard,Accessories,1,20.00,C002"
        );

        Path tempFile = Files.createTempFile("sales-test", ".csv");
        Files.writeString(tempFile, csvContent);

        try {
            // Act
            List<SalesRecord> records = analyzer.loadFromCsv(tempFile);

            // Assert
            assertEquals(2, records.size());
            assertEquals("2001", records.get(0).getOrderId());
            assertEquals("North", records.get(0).getRegion());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void totalRevenue_isCalculatedCorrectly() {
        List<SalesRecord> records = createSampleRecords();

        double totalRevenue = analyzer.calculateTotalRevenue(records);

        double expected = 3 * 25.50 + 2 * 80.00 + 1 * 220.00 + 1 * 1500.00;
        assertEquals(expected, totalRevenue, 0.0001);
    }

    @Test
    void revenueByRegion_isGroupedCorrectly() {
        List<SalesRecord> records = createSampleRecords();

        Map<String, Double> byRegion = analyzer.calculateRevenueByRegion(records);

        // There are three regions in the sample data: North, South, West
        assertEquals(3, byRegion.size());

        // North: Mouse (3*25.5) + Monitor (1*220) = 76.5 + 220 = 296.5
        assertEquals(3 * 25.50 + 220.00, byRegion.get("North"), 0.0001);
        // South: Keyboard (2*80) = 160
        assertEquals(2 * 80.00, byRegion.get("South"), 0.0001);
        // West: Gaming Laptop = 1500
        assertEquals(1500.00, byRegion.get("West"), 0.0001);
    }

    @Test
    void unitsSoldByProduct_isAggregatedCorrectly() {
        List<SalesRecord> records = createSampleRecords();

        Map<String, Integer> unitsByProduct =
                analyzer.calculateUnitsSoldByProduct(records);

        assertEquals(3, unitsByProduct.get("Wireless Mouse"));
        assertEquals(2, unitsByProduct.get("Mechanical Keyboard"));
        assertEquals(1, unitsByProduct.get("27\" Monitor"));
        assertEquals(1, unitsByProduct.get("Gaming Laptop"));
    }

    @Test
    void monthlyRevenue_isGroupedByMonth() {
        List<SalesRecord> records = createSampleRecords();

        Map<Month, Double> monthly = analyzer.calculateMonthlyRevenue(records);

        // January: records 1001 & 1002
        double jan = 3 * 25.50 + 2 * 80.00;
        // February: records 1003 & 1004
        double feb = 220.00 + 1500.00;

        assertEquals(jan, monthly.get(Month.JANUARY), 0.0001);
        assertEquals(feb, monthly.get(Month.FEBRUARY), 0.0001);
    }

    @Test
    void ordersByCategory_isCountedCorrectly() {
        List<SalesRecord> records = createSampleRecords();

        Map<String, Long> byCategory = analyzer.countOrdersByCategory(records);

        // Accessories: first two records
        assertEquals(2L, byCategory.get("Accessories"));
        // Electronics: last two records
        assertEquals(2L, byCategory.get("Electronics"));
    }

    @Test
    void averageOrderValuePerCustomer_isCorrect() {
        List<SalesRecord> records = createSampleRecords();

        Map<String, Double> avgPerCustomer =
                analyzer.calculateAverageOrderValuePerCustomer(records);

        // Customer C001 made two orders:
        // Mouse (3*25.5 = 76.5) and Monitor (220)
        double c001Order1 = 3 * 25.50;
        double c001Order2 = 220.00;
        double expectedAvgC001 = (c001Order1 + c001Order2) / 2.0;

        assertEquals(expectedAvgC001, avgPerCustomer.get("C001"), 0.0001);

        // C002: one order: 2 * 80 = 160
        assertEquals(2 * 80.00, avgPerCustomer.get("C002"), 0.0001);

        // C003: one order: 1500
        assertEquals(1500.00, avgPerCustomer.get("C003"), 0.0001);
    }

    @Test
    void largestOrder_isFoundCorrectly() {
        List<SalesRecord> records = createSampleRecords();

        var largestOpt = analyzer.findLargestOrderByRevenue(records);

        assertTrue(largestOpt.isPresent());
        SalesRecord largest = largestOpt.get();
        assertEquals("1004", largest.getOrderId());
        assertEquals(1500.00, largest.getRevenue(), 0.0001);
    }

    @Test
    void revenueSummary_hasCorrectStatistics() {
        List<SalesRecord> records = createSampleRecords();

        var summary = analyzer.revenueSummary(records);

        // Revenues: 76.5, 160, 220, 1500
        assertEquals(4, summary.getCount());
        assertEquals(76.50, summary.getMin(), 0.0001);
        assertEquals(1500.00, summary.getMax(), 0.0001);
        assertEquals(1956.50, summary.getSum(), 0.0001);
        assertEquals(489.125, summary.getAverage(), 0.0001);
    }
}
