import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command-Line Interface (CLI) for the BatStateU Library Borrow-and-Return Monitoring System.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibrarySystem librarySystem = new LibrarySystem();

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("==========================================================");
        System.out.println("   BATANGAS STATE UNIVERSITY - ALANGILAN CAMPUS (CICS)    ");
        System.out.println("       LIBRARY BORROW-AND-RETURN MONITORING SYSTEM        ");
        System.out.println("==========================================================");
        librarySystem.displayLibraryPolicy();

        while (running) {
            System.out.println("\n--- MAIN MENU: SELECT ROLE ---");
            System.out.println("[1] Librarian Portal");
            System.out.println("[2] Student Portal");
            System.out.println("[0] Save & Exit System");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        runLibrarianMenu();
                        break;
                    case 2:
                        runStudentMenu();
                        break;
                    case 0:
                        librarySystem.saveAllData();
                        running = false;
                        System.out.println("\nAll records saved to files (books.txt, students.txt, records.txt). Goodbye!");
                        break;
                    default:
                        System.out.println("[!] Invalid option. Please choose 1, 2, or 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a numeric menu option.");
            }
        }
        scanner.close();
    }

    private static void runLibrarianMenu() {
        Librarian librarian = librarySystem.getDefaultLibrarian();
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n================ LIBRARIAN MENU ================");
            System.out.println("[1]  View All Books in Catalog");
            System.out.println("[2]  Search Book");
            System.out.println("[3]  Add New Book");
            System.out.println("[4]  Edit Book Details");
            System.out.println("[5]  Issue / Borrow Book to Student");
            System.out.println("[6]  Process Book Return & Overdue Fine");
            System.out.println("[7]  View All Currently Checked-Out Books");
            System.out.println("[8]  View Unreturned Overdue Books & Fines");
            System.out.println("[9]  Register / Authorize New Student");
            System.out.println("[10] Delete / Remove Student");
            System.out.println("[11] View All Registered Students");
            System.out.println("[12] View Library Borrowing & Fine Policy");
            System.out.println("[13] View Librarian Profile");
            System.out.println("[0]  Back to Main Menu");
            System.out.print("Select operation: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        librarySystem.viewAllBooks();
                        break;
                    case 2:
                        handleSearchBook();
                        break;
                    case 3:
                        handleAddBook();
                        break;
                    case 4:
                        handleEditBook();
                        break;
                    case 5:
                        handleIssueBook();
                        break;
                    case 6:
                        handleProcessReturn();
                        break;
                    case 7:
                        librarySystem.viewCheckedOut();
                        break;
                    case 8:
                        librarySystem.viewOverdueRecords();
                        break;
                    case 9:
                        handleRegisterStudent();
                        break;
                    case 10:
                        handleDeleteStudent();
                        break;
                    case 11:
                        librarySystem.viewAllStudents();
                        break;
                    case 12:
                        librarySystem.displayLibraryPolicy();
                        break;
                    case 13:
                        if (librarian != null) {
                            librarian.displayInfo();
                        }
                        break;
                    case 0:
                        inMenu = false;
                        break;
                    default:
                        System.out.println("[!] Invalid choice. Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid number.");
            }
        }
    }

    private static void runStudentMenu() {
        System.out.print("\nEnter Student SR-Code: ");
        String srCode = scanner.nextLine().trim();

        Student student = librarySystem.findStudentBySrCode(srCode);
        if (student == null) {
            System.out.println("\n[!] ACCESS DENIED: SR-Code '" + srCode + "' is not registered in the library system.");
            System.out.println("    Please proceed to the Librarian counter first to register your SR-Code and Full Name.");
            return;
        }

        if (librarySystem.hasOverdueBooks(student.getSrCode())) {
            System.out.println("\n[!] WARNING: You have an UNRETURNED OVERDUE book! Please check option [5] and return it promptly.");
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========= STUDENT MENU (" + student.getSrCode() + ") =========");
            System.out.println("[1] View My Student Profile");
            System.out.println("[2] View Book Catalog");
            System.out.println("[3] Search Book");
            System.out.println("[4] Request Book Check-Out (Borrow)");
            System.out.println("[5] View My Borrowing History & Due Dates");
            System.out.println("[6] View Library Borrowing & Fine Policy");
            System.out.println("[0] Logout to Main Menu");
            System.out.print("Select operation: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        student.displayInfo();
                        break;
                    case 2:
                        librarySystem.viewAllBooks();
                        break;
                    case 3:
                        handleSearchBook();
                        break;
                    case 4:
                        System.out.print("Enter Book ID to borrow (e.g., B001): ");
                        String bookId = scanner.nextLine().trim();
                        librarySystem.issueBook(bookId, student.getSrCode());
                        break;
                    case 5:
                        librarySystem.viewStudentHistory(student.getSrCode());
                        break;
                    case 6:
                        librarySystem.displayLibraryPolicy();
                        break;
                    case 0:
                        inMenu = false;
                        break;
                    default:
                        System.out.println("[!] Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a numeric menu option.");
            }
        }
    }

    private static void handleRegisterStudent() {
        System.out.println("\n--- STUDENT LIBRARY REGISTRATION (LIBRARIAN) ---");
        System.out.print("Enter Student SR-Code (e.g., 25-12345): ");
        String srCode = scanner.nextLine().trim();
        System.out.print("Enter Student Full Name (Last, First M.I.): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Course/Program (press Enter for BS Information Technology): ");
        String course = scanner.nextLine().trim();

        librarySystem.registerStudent(srCode, name, course);
    }

    private static void handleDeleteStudent() {
        librarySystem.viewAllStudents();
        System.out.println("\n--- DELETE / REMOVE STUDENT (LIBRARIAN) ---");
        System.out.print("Enter SR-Code of student to delete: ");
        String srCode = scanner.nextLine().trim();

        Student target = librarySystem.findStudentBySrCode(srCode);
        if (target == null) {
            System.out.println("[!] Error: Student with SR-Code '" + srCode + "' not found.");
            return;
        }

        System.out.print("Are you sure you want to delete " + target.getName() + " (" + target.getSrCode() + ")? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            librarySystem.deleteStudent(srCode);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void handleSearchBook() {
        System.out.print("Enter keyword (Book ID, Title, or Author): ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = librarySystem.searchBook(keyword);

        System.out.println("\n--- SEARCH RESULTS (" + results.size() + " found) ---");
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            for (Book book : results) {
                System.out.println(book.getDetails());
            }
        }
    }

    private static void handleAddBook() {
        System.out.print("Enter New Book ID (e.g., B006): ");
        String bookId = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine().trim();

        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty()) {
            System.out.println("[!] Error: All book fields are required.");
            return;
        }
        librarySystem.addBook(new Book(bookId, title, author));
    }

    private static void handleEditBook() {
        System.out.print("Enter Book ID to edit: ");
        String bookId = scanner.nextLine().trim();
        Book existing = librarySystem.findBookById(bookId);
        if (existing == null) {
            System.out.println("[!] Error: Book ID not found.");
            return;
        }

        System.out.println("Current Details: " + existing.getDetails());
        System.out.print("Enter New Title (press Enter to keep current): ");
        String newTitle = scanner.nextLine().trim();
        System.out.print("Enter New Author (press Enter to keep current): ");
        String newAuthor = scanner.nextLine().trim();

        if (librarySystem.editBook(bookId, newTitle, newAuthor)) {
            System.out.println("[+] Book details updated and saved successfully.");
        }
    }

    private static void handleIssueBook() {
        System.out.print("Enter Book ID to issue (e.g., B001): ");
        String bookId = scanner.nextLine().trim();
        System.out.print("Enter Student SR-Code: ");
        String srCode = scanner.nextLine().trim();
        librarySystem.issueBook(bookId, srCode);
    }

    private static void handleProcessReturn() {
        librarySystem.viewCheckedOut();
        System.out.print("\nEnter Borrow Record ID to return (e.g., R001 or R002): ");
        String recordId = scanner.nextLine().trim();

        System.out.print("Enter Return Date (YYYY-MM-DD) or press Enter for today (" + LocalDate.now() + "): ");
        String dateInput = scanner.nextLine().trim();

        try {
            LocalDate returnDate = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);
            librarySystem.processReturn(recordId, returnDate);
        } catch (DateTimeParseException e) {
            System.out.println("[!] Invalid date format. Please use YYYY-MM-DD (e.g., 2026-11-25).");
        }
    }
} 