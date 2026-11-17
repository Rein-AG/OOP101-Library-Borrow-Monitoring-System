/**
 * Represents a college student borrower extending the abstract User class.
 */
public class Student extends User {
    private String srCode;
    private String course;
    private int borrowedBooksCount;
    public static final int MAX_BORROW_LIMIT = 3;

    public Student(String userId, String name, String email, String srCode, String course) {
        super(userId, name, email);
        this.srCode = srCode;
        this.course = course;
        this.borrowedBooksCount = 0;
    }

    public String getSrCode() {
        return srCode;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }

    public boolean canBorrow() {
        return borrowedBooksCount < MAX_BORROW_LIMIT;
    }

    public void updateBorrowCount(int delta) {
        this.borrowedBooksCount = Math.max(0, this.borrowedBooksCount + delta);
    }

    @Override
    public String getProfile() {
        return String.format("SR-Code: %s | Name: %s | Course: %s | Email: %s | Active Loans: %d/%d",
                srCode, getName(), course, getEmail(), borrowedBooksCount, MAX_BORROW_LIMIT);
    }

    @Override
    public void displayInfo() {
        System.out.println("==========================================");
        System.out.println("              STUDENT PROFILE             ");
        System.out.println("==========================================");
        System.out.println("User ID        : " + getUserId());
        System.out.println("SR-Code        : " + srCode);
        System.out.println("Full Name      : " + getName());
        System.out.println("Course/Program : " + course);
        System.out.println("Email          : " + getEmail());
        System.out.println("Books Borrowed : " + borrowedBooksCount + " / " + MAX_BORROW_LIMIT);
        System.out.println("==========================================");
    }
}