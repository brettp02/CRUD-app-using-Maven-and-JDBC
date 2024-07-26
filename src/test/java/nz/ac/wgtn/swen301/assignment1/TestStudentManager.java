package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;

/**
 * Unit tests for StudentManager, to be extended.
 */
public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @BeforeEach
    public  void init () {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    @Test
    public void dummyTest() throws Exception {
        Student student = new StudentManager().fetchStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    /**
     * if no record with such an id exists in the database
     */
    @Test
    public void testFetchStudent1() throws Exception {
        Student student = new StudentManager().fetchStudent("id2");
        assertNotNull(student);
        assertEquals("id2",student.getId());
    }

    @Test
    public void testFetchStudent2() throws Exception {
        assertThrows(NoSuchRecordException.class, () -> {StudentManager.fetchStudent("invalid_id");});
    }

    /**
     * if no record with such an id exists in the database
     */
    @Test
    public void testFetchDegree1() throws Exception {
        Degree degree = new StudentManager().fetchDegree("deg5");
        assertNotNull(degree);
        assertEquals("deg5",degree.getId());
    }

    @Test
    public void testFetchDegree2() throws Exception {
        assertThrows(NoSuchRecordException.class, () -> {StudentManager.fetchDegree("invalid_degree");});
    }


    /**
     * if no record corresponding to this student instance exists in the database
     */
    @Test
    public void testRemove() throws Exception {
        Student student = new StudentManager().fetchStudent("id6");
        assertNotNull(student);
        StudentManager.remove(student);
        assertThrows(NoSuchRecordException.class, () -> {StudentManager.fetchStudent("id6");});
    }


    /**
     * if no record corresponding to this student instance exists in the database
     */
    @Test
    public void testUpdate() throws Exception {
        Student student = new StudentManager().fetchStudent("id2");
        student.setName("New");
        StudentManager.update(student);
        Student updated = new StudentManager().fetchStudent("id2");
        assertEquals("New",updated.getName());
    }


    @Test
    public void testNewStudent() throws Exception {
        Collection<String> studentsBefore = StudentManager.fetchAllStudentIds();

        Degree degree = new StudentManager().fetchDegree("deg2");
        Student student = StudentManager.newStudent("Name","FirstN", degree);

        Collection<String> studentsAfter = StudentManager.fetchAllStudentIds();

        String newId = student.getId();

        assertNotNull(student);
        assertEquals("Name",student.getName());
        assertEquals("FirstN",student.getFirstName());
        assert student.getFirstName().length() < 10 && student.getName().length() < 10;
        assert !studentsBefore.contains(newId);
        assert studentsAfter.size() > studentsBefore.size();
    }

    @Test
    public void testFetchAllStudentIds() throws Exception {
        Collection<String> studentIds = new StudentManager().fetchAllStudentIds();
        assertNotNull(studentIds);
        assert studentIds.size() > 0;
    }

}
