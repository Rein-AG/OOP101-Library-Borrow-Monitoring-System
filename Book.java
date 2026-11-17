/**
 * Represents an individual book in the library catalog.
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private boolean isBorrowed;

    public Book(String bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    // Encapsulated Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public boolean isAvailable() {
        return !isBorrowed;
    }

    // State transition methods
    public void markAsBorrowed() {
        this.isBorrowed = true;
    }

    public void markAsReturned() {
        this.isBorrowed = false;
    }

    public String getDetails() {
        String status = isBorrowed ? "BORROWED" : "AVAILABLE";
        return String.format("[%-6s] %-35s | Author: %-22s | Status: %s",
                bookId, title, author, status);
    }
}