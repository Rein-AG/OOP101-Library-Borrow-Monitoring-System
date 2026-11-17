import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Core controller managing the book catalog, user accounts, borrow/return logs,
 * real-time overdue detection, and basic text-file persistence.
 */
public class LibrarySystem implements LibraryOperations {
    private ArrayList<Book> bookCatalog;
    private ArrayList<BorrowRecord> borrowRecords;
    private ArrayList<Student> studentList;
    private ArrayList<Librarian> librarianList;
    private int recordSequence;
    private int studentSequence;

    // Clearly defined borrowing period and fine rate policies
    public static final int DEFAULT_LOAN_DAYS = 7;
    public static final double DAILY_FINE_RATE = BorrowRecord.DAILY_FINE_RATE; // PHP 20.00/day
    public static final int MAX_BORROW_LIMIT = Student.MAX_BORROW_LIMIT;      // 3 books max

    // Basic file paths for record persistence
    private static final String BOOKS_FILE = "books.txt";
    private static final String STUDENTS_FILE = "students.txt";
    private static final String RECORDS_FILE = "records.txt";

    public LibrarySystem() {
        this.bookCatalog = new ArrayList<>();
        this.borrowRecords = new ArrayList<>();
        this.studentList = new ArrayList<>();
        this.librarianList = new ArrayList<>();
        this.recordSequence = 1;
        this.studentSequence = 1;

        // Default librarian account
        librarianList.add(new Librarian("L001", "Alangilan Library Admin",
                "library.alangilan@g.batstate-u.edu.ph", "LIB-101", "CICS Library Services"));

        // Load saved records from files if available; otherwise initialize sample data
        if (hasSavedFiles()) {
            loadAllData();
        } else {
            initializeSampleData();
            saveAllData();
        }

        // Automatically check unreturned overdue books upon system startup
        scanActiveOverdueBooks(true);
    }

    /**
     * Displays clearly defined library borrowing rules, loan duration, and overdue fine rates.
     */
    public void displayLibraryPolicy() {
        System.out.println("----------------------------------------------------------");
        System.out.println(" LIBRARY BORROWING & PENALTY POLICY:");
        System.out.printf(" * Borrowing Period : %d Calendar Days%n", DEFAULT_LOAN_DAYS);
        System.out.printf(" * Overdue Fine Rate: PHP %.2f per day past due date%n", DAILY_FINE_RATE);
        System.out.printf(" * Borrowing Limit  : Max %d active book(s) per student%n", MAX_BORROW_LIMIT);
        System.out.println("----------------------------------------------------------");
    }

    /**
     * Pre-loads initial catalog books, student records, and sample borrow logs on first run.
     */
    private void initializeSampleData() {
        bookCatalog.add(new Book("B001", "Object-Oriented Programming in Java", "Herbert Schildt"));
        bookCatalog.add(new Book("B002", "Clean Code: Agile Software Craftsmanship", "Robert C. Martin"));
        bookCatalog.add(new Book("B003", "Data Structures and Algorithms in Java", "Robert Lafore"));
        bookCatalog.add(new Book("B004", "Database System Concepts", "Abraham Silberschatz"));
        bookCatalog.add(new Book("B005", "Introduction to Computing", "Peter Norton"));

        studentList.add(new Student(String.format("U%03d", studentSequence++), "Gamab, Rein Destine Leur A.", "25-04203@g.batstate-u.edu.ph", "25-04203", "BS Information Technology"));
        studentList.add(new Student(String.format("U%03d", studentSequence++), "Macalindol, Ryza May O.", "25-09724@g.batstate-u.edu.ph", "25-09724", "BS Information Technology"));
        studentList.add(new Student(String.format("U%03d", studentSequence++), "Pacheco, Christine Joy F.", "25-02490@g.batstate-u.edu.ph", "25-02490", "BS Information Technology"));

        // Sample loans (1 active on-time loan, 1 unreturned overdue loan)
        createInitialLoan("B002", "25-04203", LocalDate.now().minusDays(2), LocalDate.now().plusDays(5));
        createInitialLoan("B004", "25-09724", LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
    }

    private void createInitialLoan(String bookId, String srCode, LocalDate borrowDate, LocalDate dueDate) {
        Book book = findBookById(bookId);
        Student student = findStudentBySrCode(srCode);
        if (book != null && student != null) {
            String recId = String.format("R%03d", recordSequence++);
            BorrowRecord record = new BorrowRecord(recId, bookId, srCode, borrowDate, dueDate);
            record.checkOverdue();
            borrowRecords.add(record);
            book.markAsBorrowed();
            student.updateBorrowCount(1);
        }
    }

    // =========================================================================
    // BASIC FILE SAVING & LOADING (PRESERVES BOOKS, STUDENTS, AND RECORDS)
    // =========================================================================

    private boolean hasSavedFiles() {
        return new File(BOOKS_FILE).exists()
                && new File(STUDENTS_FILE).exists()
                && new File(RECORDS_FILE).exists();
    }

    public void saveAllData() {
        saveBooksToFile();
        saveStudentsToFile();
        saveRecordsToFile();
    }

    private void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : bookCatalog) {
                writer.write(sanitize(b.getBookId()) + "|"
                        + sanitize(b.getTitle()) + "|"
                        + sanitize(b.getAuthor()) + "|"
                        + b.isBorrowed());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("[!] Warning: Failed to save books to file: " + e.getMessage());
        }
    }

    private void saveStudentsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : studentList) {
                writer.write(sanitize(s.getUserId()) + "|"
                        + sanitize(s.getSrCode()) + "|"
                        + sanitize(s.getName()) + "|"
                        + sanitize(s.getCourse()) + "|"
                        + sanitize(s.getEmail()) + "|"
                        + s.getBorrowedBooksCount());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("[!] Warning: Failed to save students to file: " + e.getMessage());
        }
    }

    private void saveRecordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECORDS_FILE))) {
            for (BorrowRecord r : borrowRecords) {
                String retDateStr = (r.getReturnDate() != null) ? r.getReturnDate().toString() : "NULL";
                writer.write(sanitize(r.getRecordId()) + "|"
                        + sanitize(r.getBookId()) + "|"
                        + sanitize(r.getSrCode()) + "|"
                        + r.getBorrowDate() + "|"
                        + r.getDueDate() + "|"
                        + retDateStr);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("[!] Warning: Failed to save borrow records to file: " + e.getMessage());
        }
    }

    private void loadAllData() {
        bookCatalog.clear();
        studentList.clear();
        borrowRecords.clear();

        // 1. Load Books
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    Book book = new Book(parts[0], parts[1], parts[2]);
                    if (Boolean.parseBoolean(parts[3])) {
                        book.markAsBorrowed();
                    }
                    bookCatalog.add(book);
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error loading books file: " + e.getMessage());
        }

        // 2. Load Students
        int maxUserNum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    Student student = new Student(parts[0], parts[2], parts[4], parts[1], parts[3]);
                    studentList.add(student);
                    try {
                        int num = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                        if (num > maxUserNum) maxUserNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error loading students file: " + e.getMessage());
        }
        this.studentSequence = maxUserNum + 1;

        // 3. Load Borrow Records & sync active borrow counts
        int maxRecNum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(RECORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    LocalDate borrowDate = LocalDate.parse(parts[3]);
                    LocalDate dueDate = LocalDate.parse(parts[4]);
                    BorrowRecord record = new BorrowRecord(parts[0], parts[1], parts[2], borrowDate, dueDate);

                    if (!"NULL".equalsIgnoreCase(parts[5])) {
                        record.completeReturn(LocalDate.parse(parts[5]));
                    } else {
                        record.checkOverdue();
                        Student borrower = findStudentBySrCode(parts[2]);
                        if (borrower != null) {
                            borrower.updateBorrowCount(1);
                        }
                    }
                    borrowRecords.add(record);

                    try {
                        int num = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                        if (num > maxRecNum) maxRecNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error loading borrow records file: " + e.getMessage());
        }
        this.recordSequence = maxRecNum + 1;
        System.out.println("[+] Saved records loaded successfully ("
                + bookCatalog.size() + " books, "
                + studentList.size() + " students, "
                + borrowRecords.size() + " transactions).");
    }

    private String sanitize(String input) {
        return input == null ? "" : input.replace("|", "-").trim();
    }

    // =========================================================================
    // STUDENT MANAGEMENT (REGISTER, DELETE, VIEW)
    // =========================================================================

    public boolean registerStudent(String srCode, String name, String course) {
        String cleanSrCode = srCode.trim();
        String cleanName = name.trim();
        String cleanCourse = course.isBlank() ? "BS Information Technology" : course.trim();

        if (cleanSrCode.isEmpty() || cleanName.isEmpty()) {
            System.out.println("[!] Error: Student SR-Code and Full Name are required.");
            return false;
        }

        if (findStudentBySrCode(cleanSrCode) != null) {
            System.out.println("[!] Error: Student with SR-Code '" + cleanSrCode + "' is already registered.");
            return false;
        }

        String userId = String.format("U%03d", studentSequence++);
        String email = cleanSrCode + "@g.batstate-u.edu.ph";
        Student newStudent = new Student(userId, cleanName, email, cleanSrCode, cleanCourse);
        studentList.add(newStudent);
        saveAllData();

        System.out.println("\n[+] Student Successfully Registered & Saved!");
        System.out.println("    User ID   : " + userId);
        System.out.println("    SR-Code   : " + cleanSrCode);
        System.out.println("    Full Name : " + cleanName);
        System.out.println("    Course    : " + cleanCourse);
        System.out.println("    Email     : " + email);
        return true;
    }

    /**
     * Deletes a registered student if they have no unreturned borrowed books.
     */
    public boolean deleteStudent(String srCode) {
        Student student = findStudentBySrCode(srCode);
        if (student == null) {
            System.out.println("[!] Error: Student with SR-Code '" + srCode + "' not found.");
            return false;
        }

        if (student.getBorrowedBooksCount() > 0 || hasActiveLoans(srCode)) {
            System.out.println("[!] Error: Cannot delete student '" + student.getName()
                    + "' because they still have unreturned borrowed book(s).");
            return false;
        }

        studentList.remove(student);
        saveAllData();
        System.out.println("[+] Student '" + student.getName() + "' (" + student.getSrCode() + ") has been deleted.");
        return true;
    }

    public void viewAllStudents() {
        System.out.println("\n--- REGISTERED / AUTHORIZED STUDENTS ---");
        if (studentList.isEmpty()) {
            System.out.println("No registered students found.");
            return;
        }
        for (Student student : studentList) {
            System.out.println(student.getProfile());
        }
    }

    // =========================================================================
    // BOOK CATALOG & BORROW/RETURN OPERATIONS
    // =========================================================================

    @Override
    public void addBook(Book book) {
        if (findBookById(book.getBookId()) != null) {
            System.out.println("[!] Error: Book ID '" + book.getBookId() + "' already exists in the catalog.");
            return;
        }
        bookCatalog.add(book);
        saveAllData();
        System.out.println("[+] Book added and saved successfully: " + book.getTitle());
    }

    public boolean editBook(String bookId, String newTitle, String newAuthor) {
        Book book = findBookById(bookId);
        if (book == null) {
            return false;
        }
        if (!newTitle.isBlank()) {
            book.setTitle(newTitle);
        }
        if (!newAuthor.isBlank()) {
            book.setAuthor(newAuthor);
        }
        saveAllData();
        return true;
    }

    @Override
    public List<Book> searchBook(String keyword) {
        List<Book> matches = new ArrayList<>();
        String query = keyword.toLowerCase().trim();
        for (Book book : bookCatalog) {
            if (book.getBookId().toLowerCase().contains(query)
                    || book.getTitle().toLowerCase().contains(query)
                    || book.getAuthor().toLowerCase().contains(query)) {
                matches.add(book);
            }
        }
        return matches;
    }

    @Override
    public boolean issueBook(String bookId, String srCode) {
        Student student = findStudentBySrCode(srCode);
        if (student == null) {
            System.out.println("[!] Error: SR-Code '" + srCode + "' is not registered. Please register the student first.");
            return false;
        }

        Book book = findBookById(bookId);
        if (book == null) {
            System.out.println("[!] Error: Book ID '" + bookId + "' does not exist.");
            return false;
        }

        if (book.isBorrowed()) {
            System.out.println("[!] Error: '" + book.getTitle() + "' is currently borrowed by another student.");
            return false;
        }

        if (!student.canBorrow()) {
            System.out.println("[!] Error: Student has reached the maximum limit of "
                    + MAX_BORROW_LIMIT + " borrowed books.");
            return false;
        }

        if (hasOverdueBooks(srCode)) {
            System.out.println("[!] Error: Student currently has an unreturned OVERDUE book. Return it first before borrowing again.");
            return false;
        }

        String recordId = String.format("R%03d", recordSequence++);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(DEFAULT_LOAN_DAYS);

        BorrowRecord newRecord = new BorrowRecord(recordId, book.getBookId(), student.getSrCode(), borrowDate, dueDate);
        borrowRecords.add(newRecord);
        book.markAsBorrowed();
        student.updateBorrowCount(1);
        saveAllData();

        System.out.println("\n[+] Borrow Transaction Completed & Saved!");
        System.out.println("    Record ID   : " + recordId);
        System.out.println("    Book        : " + book.getTitle() + " (" + book.getBookId() + ")");
        System.out.println("    Borrower    : " + student.getName() + " (" + student.getSrCode() + ")");
        System.out.println("    Borrow Date : " + borrowDate);
        System.out.println("    Due Date    : " + dueDate + " (" + DEFAULT_LOAN_DAYS + "-day loan period)");
        System.out.printf("    Penalty Rule: PHP %.2f per day if returned late%n", DAILY_FINE_RATE);
        return true;
    }

    @Override
    public boolean processReturn(String recordId, LocalDate returnDate) {
        BorrowRecord record = findRecordById(recordId);
        if (record == null) {
            System.out.println("[!] Error: Borrow Record ID '" + recordId + "' not found.");
            return false;
        }

        if (!record.isActive()) {
            System.out.println("[!] Error: Record '" + recordId + "' has already been returned on " + record.getReturnDate() + ".");
            return false;
        }

        if (returnDate.isBefore(record.getBorrowDate())) {
            System.out.println("[!] Error: Return date cannot be earlier than the borrow date (" + record.getBorrowDate() + ").");
            return false;
        }

        record.completeReturn(returnDate);

        Book book = findBookById(record.getBookId());
        if (book != null) {
            book.markAsReturned();
        }

        Student student = findStudentBySrCode(record.getSrCode());
        if (student != null) {
            student.updateBorrowCount(-1);
        }

        saveAllData();

        System.out.println("\n[+] Book Return Processed & Saved!");
        System.out.println("    Record ID   : " + record.getRecordId());
        System.out.println("    Book ID     : " + record.getBookId());
        System.out.println("    Due Date    : " + record.getDueDate());
        System.out.println("    Return Date : " + record.getReturnDate());
        if (record.isOverdue()) {
            System.out.printf("    Status      : OVERDUE (%d day/s late) | Late Penalty Fee: PHP %.2f%n",
                    record.getDaysOverdue(), record.getFineAmount());
        } else {
            System.out.println("    Status      : RETURNED ON TIME (No Penalty)");
        }
        return true;
    }

    /**
     * Proactively scans all unreturned books to detect and flag overdue loans in real time.
     */
    public int scanActiveOverdueBooks(boolean printAlertBanner) {
        int overdueCount = 0;
        for (BorrowRecord record : borrowRecords) {
            if (record.isActive() && record.checkOverdue()) {
                overdueCount++;
            }
        }
        if (printAlertBanner && overdueCount > 0) {
            System.out.println("\n[!] OVERDUE ALERT: There is/are " + overdueCount
                    + " unreturned book(s) currently past due date!");
        }
        return overdueCount;
    }

    @Override
    public void viewCheckedOut() {
        scanActiveOverdueBooks(false);
        System.out.println("\n--- CURRENTLY CHECKED-OUT BOOKS (REAL-TIME OVERDUE CHECK) ---");
        boolean found = false;
        for (BorrowRecord record : borrowRecords) {
            if (record.isActive()) {
                Book book = findBookById(record.getBookId());
                Student student = findStudentBySrCode(record.getSrCode());
                String title = (book != null) ? book.getTitle() : "Unknown";
                String studentName = (student != null) ? student.getName() : "Unknown";

                System.out.println(record.getSummary());
                System.out.println("         -> Title: " + title + " | Borrower: " + studentName);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books are currently checked out.");
        }
    }

    public void viewOverdueRecords() {
        System.out.println("\n--- UNRETURNED OVERDUE BOOKS & ACCRUED FINES ---");
        System.out.printf("(Rate: PHP %.2f/day after %d-day borrowing period)%n", DAILY_FINE_RATE, DEFAULT_LOAN_DAYS);
        boolean found = false;
        for (BorrowRecord record : borrowRecords) {
            if (record.isActive() && record.checkOverdue()) {
                Book book = findBookById(record.getBookId());
                Student student = findStudentBySrCode(record.getSrCode());
                String title = (book != null) ? book.getTitle() : "Unknown";
                String studentName = (student != null) ? student.getName() : "Unknown";

                System.out.println(record.getSummary());
                System.out.println("         -> Title: " + title + " | Borrower: " + studentName);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No active unreturned overdue records found.");
        }
    }

    public void viewAllBooks() {
        System.out.println("\n--- LIBRARY BOOK CATALOG ---");
        if (bookCatalog.isEmpty()) {
            System.out.println("The catalog is currently empty.");
            return;
        }
        for (Book book : bookCatalog) {
            System.out.println(book.getDetails());
        }
    }

    public void viewStudentHistory(String srCode) {
        System.out.println("\n--- BORROWING HISTORY & LIVE OVERDUE STATUS FOR SR-CODE: " + srCode + " ---");
        boolean found = false;
        for (BorrowRecord record : borrowRecords) {
            if (record.getSrCode().equalsIgnoreCase(srCode)) {
                Book book = findBookById(record.getBookId());
                String title = (book != null) ? book.getTitle() : "Unknown";
                System.out.println(record.getSummary());
                System.out.println("         -> Book Title: " + title);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No borrowing records found for this student.");
        }
    }

    public boolean hasOverdueBooks(String srCode) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getSrCode().equalsIgnoreCase(srCode) && record.isActive() && record.checkOverdue()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasActiveLoans(String srCode) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getSrCode().equalsIgnoreCase(srCode) && record.isActive()) {
                return true;
            }
        }
        return false;
    }

    public Book findBookById(String bookId) {
        for (Book book : bookCatalog) {
            if (book.getBookId().equalsIgnoreCase(bookId.trim())) {
                return book;
            }
        }
        return null;
    }

    public Student findStudentBySrCode(String srCode) {
        for (Student student : studentList) {
            if (student.getSrCode().equalsIgnoreCase(srCode.trim())) {
                return student;
            }
        }
        return null;
    }

    public BorrowRecord findRecordById(String recordId) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getRecordId().equalsIgnoreCase(recordId.trim())) {
                return record;
            }
        }
        return null;
    }

    public Librarian getDefaultLibrarian() {
        return librarianList.isEmpty() ? null : librarianList.get(0);
    }
}