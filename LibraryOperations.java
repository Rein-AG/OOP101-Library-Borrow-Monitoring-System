import java.time.LocalDate;
import java.util.List;

/**
 * Interface defining standardized library inventory and transaction operations.
 */
public interface LibraryOperations {
    void addBook(Book book);
    List<Book> searchBook(String keyword);
    boolean issueBook(String bookId, String srCode);
    boolean processReturn(String recordId, LocalDate returnDate);
    void viewCheckedOut();
}