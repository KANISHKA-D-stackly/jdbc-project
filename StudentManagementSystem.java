import java.sql.*;
import java.util.Scanner;

public class StudentManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/jdbc_projects";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Change to your MySQL password

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nSTUDENT MANAGEMENT SYSTEM");
                System.out.println("1. Add student");
                System.out.println("2. View all students");
                System.out.println("3. Search student by ID");
                System.out.println("4. Update student");
                System.out.println("5. Delete student");
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
                        case 1 -> addStudent(sc);
                        case 2 -> viewStudents();
                        case 3 -> searchStudent(sc);
                        case 4 -> updateStudent(sc);
                        case 5 -> deleteStudent(sc);
                        case 0 -> {
                            System.out.println("Thank you!");
                            return;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database error: " + e.getMessage());
                }
            }
        }
    }

    private static void addStudent(Scanner sc) throws SQLException {
        System.out.print("Student ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Student name: ");
        String name = sc.nextLine().trim();
        System.out.print("Department: ");
        String dept = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Marks: ");
        double marks = Double.parseDouble(sc.nextLine().trim());

        String sql = "insert into students(student_id, student_name, department, email, marks) values(?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setString(4, email);
            ps.setDouble(5, marks);
            System.out.println(ps.executeUpdate() > 0 ? "Student added successfully." : "Student was not added.");
        }
    }

    private static void viewStudents() throws SQLException {
        String sql = "select * from students order by student_id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\nID | Name | Department | Email | Marks");
            System.out.println("---------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%d | %s | %s | %s | %.2f%n",
                    rs.getInt("student_id"), rs.getString("student_name"),
                    rs.getString("department"), rs.getString("email"), rs.getDouble("marks"));
            }
            if (!found) System.out.println("No students found.");
        }
    }

    private static void searchStudent(Scanner sc) throws SQLException {
        System.out.print("Enter student ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        String sql = "select * from students where student_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID: " + rs.getInt("student_id"));
                    System.out.println("Name: " + rs.getString("student_name"));
                    System.out.println("Department: " + rs.getString("department"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Marks: " + rs.getDouble("marks"));
                } else {
                    System.out.println("Student not found.");
                }
            }
        }
    }

    private static void updateStudent(Scanner sc) throws SQLException {
        System.out.print("Student ID to update: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("New name: ");
        String name = sc.nextLine().trim();
        System.out.print("New department: ");
        String dept = sc.nextLine().trim();
        System.out.print("New email: ");
        String email = sc.nextLine().trim();
        System.out.print("New marks: ");
        double marks = Double.parseDouble(sc.nextLine().trim());

        String sql = "update students set student_name=?, department=?, email=?, marks=? where student_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setString(3, email);
            ps.setDouble(4, marks);
            ps.setInt(5, id);
            System.out.println(ps.executeUpdate() > 0 ? "Student updated." : "Student ID not found.");
        }
    }

    private static void deleteStudent(Scanner sc) throws SQLException {
        System.out.print("Student ID to delete: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        String sql = "delete from students where student_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            System.out.println(ps.executeUpdate() > 0 ? "Student deleted." : "Student ID not found.");
        }
    }
}
