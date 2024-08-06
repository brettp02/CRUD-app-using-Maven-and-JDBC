package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;
import java.util.Collection;
import java.sql.*;
import java.util.*;


/**
 * A student manager providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {
    private static String url =  "jdbc:derby:memory:studentdb";

    // cache/hashmap, in order to track existing students
    private static Map<String, Student> studentCache = new HashMap<>();
    private static Map<String, Degree> degreeCache = new HashMap<>();


    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE


    /**
     * Connect to DB
     * @return - Connection object
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    // THE FOLLOWING METHODS MUST BE IMPLEMENTED :

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student fetchStudent(String id) throws NoSuchRecordException {
        String query = "SELECT s.id, s.first_name, s.name, d.id AS student, d.name AS d_id, d.id AS degree " +
                        "FROM STUDENTS s " +
                        "JOIN DEGREES d ON s.degree = d.id " +
                        "WHERE s.id = ?";

        // If instance already exist return existing student
        if(studentCache.containsKey(id)){
            return studentCache.get(id);
        }

        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String studentId = rs.getString("id");
                        String firstName = rs.getString("first_name");
                        String name = rs.getString("name");
                        String degreeId = rs.getString("degree");
                        String degreeName = rs.getString("d_id");

                        Degree degree = fetchDegree(degreeId);
                        Student student = new Student(studentId, firstName, name, degree);
                        studentCache.put(studentId,student);
                        return student;
                    } else {
                        throw new NoSuchRecordException("No student found with id: " + id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NoSuchRecordException("Error fetching student with id: " + id);
        }
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree fetchDegree(String id) throws NoSuchRecordException {
        String query = "SELECT *" +
                "FROM DEGREES  " +
                "WHERE id = ?";

        // If instance with this id already exists return existing instance
        if(degreeCache.containsKey(id)){
            return degreeCache.get(id);
        }

        try (Connection con = getConnection()) {
            System.out.println("Connection successful: " + con);
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String degreeId = rs.getString("id");
                        String degreeName = rs.getString("name");

                        Degree degree = new Degree(degreeId,degreeName);
                        degreeCache.put(degreeId,degree);
                        return degree;
                    } else {
                        throw new NoSuchRecordException("No degree found with id: " + id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NoSuchRecordException("Error fetching degree with id: " + id);
        }
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {
        String query = "DELETE FROM students WHERE id = ?";

        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, student.getId());
                int rowsAffected = stmt.executeUpdate();
                studentCache.remove(student.getId());
                if (rowsAffected == 0) {
                    throw new NoSuchRecordException("No student found with id: " + student.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NoSuchRecordException("Error deleting student with id: " + student.getId(), e);
        }
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        String query = "UPDATE students SET first_name = ?, name = ?, degree = ? WHERE id = ?";

        try(Connection con = getConnection()) {
            try(PreparedStatement stmt = con.prepareStatement(query)){
                stmt.setString(1, student.getFirstName());
                stmt.setString(2, student.getName());
                stmt.setString(3, student.getDegree().getId());
                stmt.setString(4, student.getId());

                int rowsAffected = stmt.executeUpdate();
                studentCache.put(student.getId(),student);
                if (rowsAffected == 0) {
                    throw new NoSuchRecordException("No student found with id: " + student.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NoSuchRecordException("Error updating student with id: " + student.getId(), e);
        }
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name,String firstName,Degree degree) throws SQLException{
        Collection<String> currentIds = fetchAllStudentIds();
        int maxIdNumber = 0;

        // Find highest number id and add 1, similar to "SERIAL" in PostgreSQL
        for (String id : currentIds) {
            try {
                int idNumber = Integer.parseInt(id.substring(2)); // Get numeric part
                if (idNumber > maxIdNumber) {
                    maxIdNumber = idNumber;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String newId = "id" + maxIdNumber + 1;


        String query = "INSERT INTO STUDENTS (id, name, first_name, degree) values(?,?,?,?)";

        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, newId);
                stmt.setString(2, name);
                stmt.setString(3, firstName);
                stmt.setString(4, degree.getId());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Inserting student failed, no rows affected.");
                }

                // check constraints before returning student
                if(firstName.length() < 10 && name.length() < 10 && !currentIds.contains(newId)) {
                    Student student = new Student(newId, name, firstName, degree);
                    studentCache.put(newId,student);
                    return student;
                }
                else {
                    throw new SQLException("Name length or id number constraints are being violated for: " + firstName + " " + name + " with student id: "+ newId +". Constraints = names < 10 char && id must be new");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting new student with id: " + newId, e);
        }
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        String query = "SELECT * FROM STUDENTS";

        Collection<String> studentIds = new ArrayList<>();

        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        studentIds.add(rs.getString("id"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error " + e);
        }
        return studentIds;
    }
}
