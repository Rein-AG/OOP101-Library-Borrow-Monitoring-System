# OOP101: Library Borrow Monitoring System

## Project Description
The **Library Borrow Monitoring System** is a Java-based console application developed for OOP101 that automates and tracks library book borrowing and returning operations. Built around core Object-Oriented Programming principles, the system manages book catalogs, student and librarian accounts, and borrowing transaction logs using persistent text-file storage (`books.txt`, `students.txt`, and `records.txt`).

## Problem Being Addressed
Manual tracking of library book loans using physical logbooks is prone to human error, misplaced records, difficulty checking real-time book availability, and delays in monitoring unreturned books. This system solves those issues by providing a structured, automated way to log transactions, monitor book availability, and maintain accurate borrower histories.

## Project Objectives
- Automate the recording and monitoring of borrowed and returned library books.
- Maintain persistent records of books, students, and borrowing transactions using local file handling (`books.txt`, `students.txt`, `records.txt`).
- Apply fundamental Object-Oriented Programming (OOP) principles—Encapsulation, Inheritance, Polymorphism, and Abstraction—in a working Java application.
- Provide clear, role-based workflows for managing library operations and monitoring borrower activity.

## Target Users
- **Librarians:** Manage the library's book inventory, record borrow and return transactions, and monitor active or overdue borrow records.
- **Students:** Browse available books, borrow and return titles, and track their borrowing status.

## Main Features
- **Book Inventory Management (`Book.java`):** Stores and updates book details, availability status, and catalog records.
- **User & Role Management (`User.java`, `Student.java`, `Librarian.java`):** Differentiates user profiles and responsibilities between students and librarians.
- **Borrow & Return Monitoring (`BorrowRecord.java`, `LibrarySystem.java`):** Logs borrowing transactions, links borrowers to specific books, and tracks return statuses.
- **Standardized Library Operations (`LibraryOperations.java`):** Defines core library actions to ensure consistent system behavior.
- **Persistent File Storage (`books.txt`, `students.txt`, `records.txt`):** Automatically reads from and writes to text files so records are preserved across sessions.

## Technologies Used
- **Programming Language:** Java (JDK)
- **Core Concepts & Libraries:** Object-Oriented Programming (OOP), Java File I/O (`java.io`, `java.util`)
- **Data Storage:** Plain Text Files (`books.txt`, `students.txt`, `records.txt`)
- **Version Control:** Git & GitHub

## Team Members and Roles
| Name | Role | Responsibilities |
| :--- | :--- | :--- |
| **Rein-AG** *(Gamab, Rein Destine Leur A.)* | Lead Developer | System architecture, core logic (`LibrarySystem.java`), and OOP implementation |
| **Christine-FP** *(Pacheco, Christine Joy F.)* | Backend & File Handling Developer | Data persistence (`books.txt`, `students.txt`, `records.txt`) and record tracking |
| **Ryza-OM** *(Macalindol, Ryza May O.)* | QA & Documentation | System testing, debugging, sample data preparation, and documentation |

## OOP Concepts Demonstrated
- **Encapsulation:** Implemented across model classes (`Book`, `User`, `Student`, `Librarian`, and `BorrowRecord`) by declaring fields as `private` and exposing controlled access through `public` getters, setters, and validation methods.
- **Inheritance:** `Student` and `Librarian` inherit shared attributes and behaviors from the base `User` class (`extends User`), promoting code reuse.
- **Polymorphism:** Demonstrated through method overriding in subclasses (`Student` and `Librarian` customizing behaviors inherited from `User`) and polymorphic handling of user instances inside `LibrarySystem`.
- **Abstraction:** Implemented through `LibraryOperations` and the base `User` structure, separating high-level library workflows from low-level implementation details.

## Instructions for Running the Program
### Prerequisites
- Java Development Kit (JDK 8 or higher) installed on your machine.

### Setup & Execution
1. Clone the repository:
   ```bash
   git clone [https://github.com/Rein-AG/OOP101-Library-Borrow-Monitoring-System.git](https://github.com/Rein-AG/OOP101-Library-Borrow-Monitoring-System.git)
   cd OOP101-Library-Borrow-Monitoring-System