# 🧠 Log Analyzer – Java Multithreaded Log Processing System

A modular and extensible Java application for analyzing distributed system log files using multiple design patterns and multithreading.

------------------------

## 📌 Project Description

This Java-based application processes and analyzes log files in parallel using a configurable thread pool and JSON-based reports. It supports multiple analysis types such as:

- **Counting log levels**
- **Identifying most/least common log sources**
- **Detecting anomalies based on frequency and timeframe**

The design emphasizes clean architecture, flexibility, and scalability for future extensions.

------------------------

## 🛠️ Technologies & Concepts

- Java 23+  
- Multithreading (Thread Pool Executor)  
- JSON (input/output)  
- Configurable `.properties` file  
- Input validation and error handling  
- Modular object-oriented design  

------------------------

## 🧩 Design Patterns Used

| Pattern              | Usage                                                                 |
|----------------------|------------------------------------------------------------------------|
| **Factory**          | `AnalyzerFactory` – creates analyzer objects dynamically               |
| **Strategy**         | `LogAnalyzer` interface and implementations – pluggable analysis types |
| **Controller**       | `Controller` class – coordinates file reading, processing, and output  |

------------------------

## ⚙️ Configuration (config.properties)

properties:
log.directory=/path/to/logs
thread.pool.size=4
output.file=log_report.json
log.analysis=COUNT_LEVELS,FIND_COMMON_SOURCE,DETECT_ANOMALIES
log.analysis.anomalies.levels=ERROR,WARNING
log.analysis.anomalies.window=60
log.analysis.anomalies.threshold=5

------------------------

## 👨‍💻 Author

**Yair Krothamer**  
📧 yairk1998@gmail.com
🔗 [LinkedIn](https://www.linkedin.com/in/yair-krothamer-8b0448230)  
