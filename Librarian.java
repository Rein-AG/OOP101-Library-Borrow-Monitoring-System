/**
 * Represents a library staff member extending the abstract User class.
 */
public class Librarian extends User {
    private String staffId;
    private String department;

    public Librarian(String userId, String name, String email, String staffId, String department) {
        super(userId, name, email);
        this.staffId = staffId;
        this.department = department;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getProfile() {
        return String.format("Staff ID: %s | Name: %s | Department: %s | Email: %s",
                staffId, getName(), department, getEmail());
    }

    @Override
    public void displayInfo() {
        System.out.println("==========================================");
        System.out.println("             LIBRARIAN PROFILE            ");
        System.out.println("==========================================");
        System.out.println("User ID    : " + getUserId());
        System.out.println("Staff ID   : " + staffId);
        System.out.println("Full Name  : " + getName());
        System.out.println("Department : " + department);
        System.out.println("Email      : " + getEmail());
        System.out.println("==========================================");
    }
}