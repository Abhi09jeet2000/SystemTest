package org.project.sales.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single row in the sales CSV file.
 *
 * CSV columns:
 *   order_id,order_date,region,product,category,quantity,unit_price,customer_id
 *
 * Assumptions:
 * - order_date is in ISO-8601 format: yyyy-MM-dd
 * - quantity is an integer
 * - unit_price is a decimal number (dot as decimal separator)
 * - No commas inside fields (no quoted CSV fields)
 */
public class SalesRecord {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String orderId;
    private final LocalDate orderDate;
    private final String region;
    private final String product;
    private final String category;
    private final int quantity;
    private final double unitPrice;
    private final String customerId;

    public SalesRecord(String orderId,
                       LocalDate orderDate,
                       String region,
                       String product,
                       String category,
                       int quantity,
                       double unitPrice,
                       String customerId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.region = region;
        this.product = product;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.customerId = customerId;
    }

    /**
     * Parse a CSV line into a SalesRecord.
     * Expects 8 comma-separated values including header order.
     */
    public static SalesRecord fromCsvLine(String line) {
        String[] tokens = line.split(",", -1); // -1 keeps empty trailing fields
        if (tokens.length != 8) {
            throw new IllegalArgumentException("Invalid CSV line, expected 8 columns: " + line);
        }

        String orderId = tokens[0].trim();
        LocalDate orderDate = LocalDate.parse(tokens[1].trim(), DATE_FORMATTER);
        String region = tokens[2].trim();
        String product = tokens[3].trim();
        String category = tokens[4].trim();
        int quantity = Integer.parseInt(tokens[5].trim());
        double unitPrice = Double.parseDouble(tokens[6].trim());
        String customerId = tokens[7].trim();

        return new SalesRecord(orderId, orderDate, region, product, category, quantity, unitPrice, customerId);
    }

    /**
     * Revenue for this record = quantity * unitPrice.
     */
    public double getRevenue() {
        return quantity * unitPrice;
    }

    // Getters (needed by streams and tests)

    public String getOrderId() {
        return orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getRegion() {
        return region;
    }

    public String getProduct() {
        return product;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "orderId='" + orderId + '\'' +
                ", orderDate=" + orderDate +
                ", region='" + region + '\'' +
                ", product='" + product + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}

