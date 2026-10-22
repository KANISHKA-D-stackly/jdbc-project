import java.sql.*;
import java.util.Scanner;

public class EmployeeLeaveManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/jdbc_projects";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== EMPLOYEE LEAVE MANAGEMENT ===");
                System.out.println("1. Register employee");
                System.out.println("2. View employees");
                System.out.println("3. Apply for leave");
                System.out.println("4. View leave requests");
                System.out.println("5. Approve/reject leave request");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");

                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number.");
                    continue;
                }

                try {
                    switch (choice) {
                        case 1 -> addEmployee(sc);
                        case 2 -> viewEmployees();
                        case 3 -> applyLeave(sc);
                        case 4 -> viewLeaveRequests();
                        case 5 -> updateLeaveStatus(sc);
                        case 0 -> {
                            System.out.println("Thank you!");
                            return;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }
        }
    }

    private static void addEmployee(Scanner sc) throws SQLException {
        System.out.print("Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Employee name: ");
        String name = sc.nextLine().trim();
        System.out.print("Department: ");
        String dept = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        String sql = "insert into employees(employee_id, employee_name, department, email) values(?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setString(4, email);
            System.out.println(ps.executeUpdate() > 0 ? "Employee registered." : "Employee was not registered.");
        }
    }

    private static void viewEmployees() throws SQLException {
        String sql = "select * from employees order by employee_id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nID | Name | Department | Email");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%d | %s | %s | %s%n",
                    rs.getInt("employee_id"), rs.getString("employee_name"),
                    rs.getString("department"), rs.getString("email"));
            }
            if (!found) System.out.println("No employees found.");
        }
    }

    private static void applyLeave(Scanner sc) throws SQLException {
        System.out.print("Employee ID: ");
        int employeeId = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Start date (YYYY-MM-DD): ");
        Date start = Date.valueOf(sc.nextLine().trim());
        System.out.print("End date (YYYY-MM-DD): ");
        Date end = Date.valueOf(sc.nextLine().trim());
        if (end.before(start)) throw new IllegalArgumentException("End date must not be before start date.");
        System.out.print("Reason: ");
        String reason = sc.nextLine().trim();

        String sql = "insert into leave_requests(employee_id, start_date, end_date, reason) values(?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            ps.setString(4, reason);
            System.out.println(ps.executeUpdate() > 0 ? "Leave request submitted as PENDING." : "Leave request failed.");
        }
    }

    private static void viewLeaveRequests() throws SQLException {
        String sql = "select lr.request_id, e.employee_name, lr.start_date, lr.end_date, lr.reason, lr.status " +
                     "from leave_requests lr join employees e on lr.employee_id=e.employee_id order by lr.request_id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nRequest | Employee | Start | End | Reason | Status");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%d | %s | %s | %s | %s | %s%n",
                    rs.getInt("request_id"), rs.getString("employee_name"),
                    rs.getDate("start_date"), rs.getDate("end_date"),
                    rs.getString("reason"), rs.getString("status"));
            }
            if (!found) System.out.println("No leave requests found.");
        }
    }

    private static void updateLeaveStatus(Scanner sc) throws SQLException {
        System.out.print("Request ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter APPROVED or REJECTED: ");
        String status = sc.nextLine().trim().toUpperCase();
        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED.");
        }

        String sql = "update leave_requests set status=? where request_id=? and status='PENDING'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            System.out.println(ps.executeUpdate() > 0 ? "Leave request status updated." :
                "Pending request not found (it may already be processed).");
        }
    }
}
