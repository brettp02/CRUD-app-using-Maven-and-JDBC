package nz.ac.wgtn.swen301.assignment1.cli;
import java.util.*;
import java.io.*;
import nz.ac.wgtn.swen301.studentdb.*;
import nz.ac.wgtn.swen301.assignment1.StudentManager;
import org.apache.commons.cli.*;


public class StudentManagerApp {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide argument(s) and print details to the console as described in the assignment brief,
     * E.g. a user could invoke this by running "java -cp <someclasspath> <arguments></arguments>"
     *
     * the arguments that StudentManagerApp accepts are as follows:
     * -select id42  – fetch the respective student record with id id42 (or any other id), and display the result on the console (System.out) as one line containing at least id, firstname and name
     * -all – fetch all student records, and display the result on the console (System.out) as one line containing at least id, firstname and name
     * -export <file> - fetch all student records, and write them to a file named <file> in CSV format, using a comma as a separator, and the first row containing the column names separated by comma. The structure of the table should be the same as in the table with example data shown above
     * @param arg
     */
    public static void main (String[] arg) {
        // Create Options object
        Options options = new Options();

        // Add the options as required
        options.addOption("select",true,"Fetch the respective student record with id (arg)");
        options.addOption("all",false,"fetch all student records");
        options.addOption("export",true,"fetch all student records and write to a file (arg)");

        // Parsing the command line arguments
        CommandLineParser parser = new DefaultParser();
        try {
            new StudentManager();
            CommandLine cmd = parser.parse(options,arg);

            // Check if select option present
            if(cmd.hasOption("select")) {
                // Fetch respective student and display result on console
                String studentId = cmd.getOptionValue("select");

                try{
                    System.out.println(printStudent(StudentManager.fetchStudent(studentId)));
                } catch (NoSuchRecordException e) {e.printStackTrace();}
            } else if(cmd.hasOption("all")){
                printAll(StudentManager.fetchAllStudentIds());
            } else if(cmd.hasOption("export")){
                String exportFile = cmd.getOptionValue("export");

            }

        } catch (ParseException p) {
            p.printStackTrace();
        }

    }

    /**
     * Prints a single student with id, first and last name and degree
     *
     * @param s
     * @return
     */
    private static String printStudent(Student s){
        return "Student: " + s.getId() + " " + s.getName() + " " + s.getFirstName() + " " + s.getDegree().getName();
    }

    /**
     * Prints all students
     *
     * @param allStudents
     * @return
     */
    private static List<String> printAll(Collection<String> allStudents){
        List<String> students = new ArrayList<>();
        try{
            for(String s : allStudents){
                students.add(printStudent(StudentManager.fetchStudent(s)));
            }
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
        }

        System.out.println(students);
        return students;
    }

    private static void exportCsv(String filename) {

    }
}
