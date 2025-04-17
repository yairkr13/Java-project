# Log Analyzer

## Authors
- Omer Shimoni
- Yair Krothamer

  Email:  
  - yairkr@edu.jmc.ac.il
  - omershi@edu.jmc.ac.il

## Design Patterns Used

1. **Factory Pattern**  
   - **Used In:** `AnalyzerFactory`  
   - **Why:** Responsible for creating analyzers based on configuration  
   - **Problem Solved:** Allows flexible extension of new analyzers without changing main code

2. **Strategy Pattern (via Interface)**  
   - **Used In:** `LogAnalyzer` interface and its implementations  
   - **Why:** Enables different types of analysis with a common interface  
   - **Problem Solved:** Supports easy swapping or adding of analyzers

3. **Controller Pattern**  
   - **Used In:** `Controller` class  
   - **Why:** Coordinates between config, processing, and reporting  
   - **Problem Solved:** Keeps `Main` class clean and separates orchestration

## Extensibility

To extend the application:
- Add a new class implementing `LogAnalyzer`
- Update `AnalyzerFactory` to return the new analyzer based on config
- For new output types (e.g., text, email), create a new report writer (e.g., `TextReportBuilder`)
