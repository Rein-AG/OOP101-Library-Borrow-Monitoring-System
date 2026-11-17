import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tracks individual book borrowing transactions, due dates, overdue flags, and late fines.
 */
public class BorrowRecord {
    private String recordId;
    private String bookId;
    private String srCode;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isOverdue;
    private double fineAmount;

    public static final double DAILY_FINE_RATE = 20.00; // PHP 20.00 per overdue day

    public BorrowRecord(String recordId, String bookId, String srCode, LocalDate borrowDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.srCode = srCode;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.isOverdue = false;
        this.fineAmount = 0.0;
        checkOverdue();
    }

    public String getRecordId() {
        return recordId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getSrCode() {
        return srCode;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isOverdue() {
        checkOverdue();
        return isOverdue;
    }

    public double getFineAmount() {
        calculateFine();
        return fineAmount;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * Returns the number of days past the due date (works even before the book is returned).
     */
    public long getDaysOverdue() {
        LocalDate referenceDate = (returnDate != null) ? returnDate : LocalDate.now();
        if (referenceDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, referenceDate);
        }
        return 0;
    }

    /**
     * Checks whether the record is past its due date even while still checked out.
     */
    public boolean checkOverdue() {
        LocalDate referenceDate = (returnDate != null) ? returnDate : LocalDate.now();
        this.isOverdue = referenceDate.isAfter(dueDate);
        if (this.isOverdue) {
            calculateFine();
        } else {
            this.fineAmount = 0.0;
        }
        return this.isOverdue;
    }

    /**
     * Calculates the overdue penalty balance based on days past the due date.
     */
    public double calculateFine() {
        long daysOverdue = getDaysOverdue();
        if (daysOverdue > 0) {
            this.fineAmount = daysOverdue * DAILY_FINE_RATE;
        } else {
            this.fineAmount = 0.0;
        }
        return this.fineAmount;
    }

    /**
     * Finalizes the return date and evaluates final overdue fine status.
     */
    public void completeReturn(LocalDate actualReturnDate) {
        this.returnDate = actualReturnDate;
        checkOverdue();
        calculateFine();
    }

    public String getSummary() {
        checkOverdue();
        long daysLate = getDaysOverdue();
        String status = isActive()
                ? (isOverdue
                    ? String.format("OVERDUE - UNRETURNED (%d day/s late | Running Fine: PHP %.2f)", daysLate, fineAmount)
                    : "ACTIVE (On Time)")
                : (isOverdue
                    ? String.format("RETURNED LATE (%d day/s late | Fine: PHP %.2f)", daysLate, fineAmount)
                    : "RETURNED ON TIME");

        return String.format("[%-6s] Book: %-6s | SR-Code: %-9s | Borrowed: %s | Due: %s | Returned: %-10s | %s",
                recordId,
                bookId,
                srCode,
                borrowDate,
                dueDate,
                (returnDate != null ? returnDate.toString() : "N/A"),
                status);
    }
}