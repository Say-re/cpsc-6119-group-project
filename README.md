# Candy Factory – CPSC 6119 Group Project

### Team Members  
- **Kelly Payne**  
- **Abdulraheem Giwa**  
- **Travis Dagostino**

---

## Project Overview
**Candy Factory** is a Java-based simulation that models how a candy store manages both in-person and online sales.  
The system allows customers to browse products, place orders, and track deliveries while the store manages inventory, processes orders, and applies promotional features.

The project is structured with a strong focus on **Object-Oriented Design (OOD)** principles and uses several core **design patterns** to ensure flexibility, maintainability, and scalability.

---

## Key Features
- Manage and simulate candy production and packaging
- Track store inventory and sales
- Handle different candy types (Chocolate, Gummy, Hard Candy, etc.)
- Checkout and order management
- Support for in-person and online store flow
- Apply promotions, discounts, and packaging decorators

---

## Design Patterns Implemented
| Pattern | Purpose |
|----------|----------|
| **Factory Pattern** | Dynamically creates different candy types (e.g., Chocolate, Gummy, Hard Candy). |
| **Observer Pattern** | Notifies users/systems when inventory levels change or when an order is updated. |
| **Singleton Pattern** | Ensures one instance of the main database/order manager. |
| **Decorator Pattern** | Adds extra features to candies (e.g., packaging, discounts, or seasonal themes) without altering core classes. |

---

## System Components
- **Model Classes:** Candy, CandyFactory, CandyPackage, Checkout, Inventory, ShoppingCart  
- **Shop Logic:** CandyStore handles operations for both in-person and online sales  
- **Command Logic:** Pricing and regular pricing commands for adjusting candy prices  
- **Utility Components:** Catalog, Inventory management, and Order tracking modules  

---

## Development Details
- **Language:** Java  
- **IDE Compatibility:** BlueJ, Visual Studio Code, IntelliJ  
- **Build Tool (Recommended):** Gradle or Maven  
- **Version Control:** Git & GitHub (branch-based workflow)  

---

## Branching & Collaboration Workflow
Each team member works on their **own feature branch**, then submits a **Pull Request (PR)** for review before merging into `main`.

