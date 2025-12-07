# Coding Challenge – Producer–Consumer & Sales Analysis (Java)

This repository contains solutions for two assignments implemented in Java:

1. **Assignment 1 – Producer–Consumer Pattern**
2. **Assignment 2 – Sales Data Analysis using Streams (project name: `sales`)**

Both projects are Maven-based and include unit tests and console output.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Assignment 1 – Producer–Consumer](#assignment-1--producerconsumer)
  - [Overview](#overview-1)
  - [How to Build & Run](#how-to-build--run)
  - [Sample Output](#sample-output)
- [Assignment 2 – Sales Analysis (Streams)](#assignment-2--sales-analysis-streams)
  - [CSV Dataset & Assumptions](#csv-dataset--assumptions)
  - [Implemented Analyses](#implemented-analyses)
  - [How to Build & Run](#how-to-build--run-1)
  - [Sample Output](#sample-output-1)
- [Submission Notes](#submission-notes)

---

## Prerequisites

- Java **17** (confirmed with `java -version`)
- Maven 3.x (confirmed with `mvn -v`)
- Git (for cloning / working with this repo)

---

## Project Structure

```text
.
├── assignment1/         # Producer–Consumer pattern
│   ├── pom.xml
│   └── src/...
├── assignment2/               # CSV + Streams analysis
│   ├── pom.xml
│   └── src/...
└── README.md

```


## Assignment 1

### Overview
- Assignment 1 implements a multithreaded producer–consumer pattern demonstrating:
- Thread synchronization with synchronized
- Blocking behavior using wait() & notifyAll()
- A custom BoundedBlockingQueue
- Producer and Consumer classes running on separate threads
- A “poison pill” sentinel value for clean termination
- Source → Queue → Destination data flow
- Core Classes (names may vary based on your package):
  ...common.BoundedBlockingQueue
  ...producer.Producer
  ...consumer.Consumer
  ...app.ProducerConsumerDemo (main class)
- Producer adds items to the queue.
- Consumer removes items until it receives the poison pill.

### How to Build & Run
```text
cd assignment1
mvn clean test
mvn package

java -cp target/assignment1-1.0-SNAPSHOT.jar com.example.producerconsumer.app.ProducerConsumerDemo
```

### Sample output
```text
[Producer-Thread] Producing: 1
[Consumer-Thread] Consuming: 1
[Producer-Thread] Producing: 2
[Consumer-Thread] Consuming: 2
...
[Producer-Thread] Producing poison pill: -1
[Consumer-Thread] Received poison pill, stopping

=== Final Results ===
Source:      [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Destination: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

```

## Assignment 2

### CSV Dataset & Assumptions
```text
sales/src/main/resources/sales_data.csv

order_id,order_date,region,product,category,quantity,unit_price,customer_id

```
- Assumptions:
order_date uses yyyy-MM-dd
No quoted fields or commas inside fields
Quantities are integers
Price values use decimal . notation
Dataset is small and fits in memory

- Sample Rows
```text
1001,2024-01-05,North,Wireless Mouse,Accessories,3,25.50,C001
1002,2024-01-06,South,Mechanical Keyboard,Accessories,2,80.00,C002
1003,2024-02-01,West,27" Monitor,Electronics,1,220.00,C003

```

### Implemented Analyses
org.project.sales.analysis.SalesDataAnalyzer
Methods Implemented with Java Streams
- loadFromCsv(Path)
- calculateTotalRevenue(List<SalesRecord>)
- calculateRevenueByRegion(List<SalesRecord>)
- calculateUnitsSoldByProduct(List<SalesRecord>)
- calculateMonthlyRevenue(List<SalesRecord>)
- countOrdersByCategory(List<SalesRecord>)
- calculateAverageOrderValuePerCustomer(List<SalesRecord>)
- findLargestOrderByRevenue(List<SalesRecord>)
- revenueSummary(List<SalesRecord>)

### How to Build & Run
```text
cd sales
mvn clean test
mvn package

java -cp target/sales-1.0-SNAPSHOT.jar org.project.sales.app.SalesAnalysisApp src/main/resources/sales_data.csv
```

### Sample Output
``` text
Loaded 10 records from src/main/resources/sales_data.csv
====================================================
Total revenue: 3436.48

Revenue by region:
  North: 541.50
  South: 1660.00
  West: 619.98
  EMEA: 615.00

Units sold by product:
  Wireless Mouse: 13
  Mechanical Keyboard: 7
  USB-C Hub: 1
  27" Monitor: 1
  Laptop Stand: 4
  Gaming Laptop: 1
  Noise Cancelling Headphones: 2
  Webcam: 5

Monthly revenue:
  JANUARY: 281.50
  FEBRUARY: 340.00
  MARCH: 2199.98
  APRIL: 615.00

Order count by category:
  Accessories: 7
  Electronics: 3

Average order value per customer:
  C001: 140.50
  C002: 279.99
  C003: 220.00
  C004: 120.00
  C005: 1500.00
  C006: 307.50

Largest order by revenue:
  Order ID: 1006, Customer: C005, Revenue: 1500.00

Revenue summary statistics:
  Count: 10
  Min: 45.00
  Max: 1500.00
  Avg: 343.65
  Sum: 3436.48

```
