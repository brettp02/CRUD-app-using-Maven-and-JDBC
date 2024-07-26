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

        try (Connection con = getConnection()) {
            System.out.println("Connection successful: " + con);
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, id);
                System.out.println("PreparedStatement successful: " + stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String studentId = rs.getString("id");
                        String firstName = rs.getString("first_name");
                        String name = rs.getString("name");
                        String degreeId = rs.getString("degree");
                        String degreeName = rs.getString("d_id");

                        System.out.println("Retrieved student details - ID: " + studentId + ", First Name: " + firstName + ", Name: " + name + ", Degree ID: " + degreeId + ", Degree Name: " + degreeName);

                        Degree degree = new Degree(degreeId, degreeName);
                        return new Student(studentId, firstName, name, degree);
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
        return null;
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {}

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
    public static void update(Student student) throws NoSuchRecordException {}


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
    public static Student newStudent(String name,String firstName,Degree degree) {
        return null;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        return null;
    }



}
