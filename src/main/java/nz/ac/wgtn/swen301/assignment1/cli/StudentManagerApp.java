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
                exportCsv(exportFile,StudentManager.fetchAllStudentIds());
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

    private static List<List<String>> getAllStudents(Collection<String> allStudents){
        List<List<String>> students = new ArrayList<>();
        try {
            for (String s : allStudents) {
                List<String> attributes = new ArrayList<>();
                Student student = StudentManager.fetchStudent(s);
                attributes.add(student.getId());
                attributes.add(student.getName());
                attributes.add(student.getFirstName());
                attributes.add(student.getDegree().getId());
                students.add(attributes);
            }
        } catch (NoSuchRecordException e) {
            e.printStackTrace();
        }

        return students;
    }

    private static void exportCsv(String filename,  Collection<String> allStudents) {
        List<List<String>> values = getAllStudents(allStudents);

        File file = new File(filename + ".csv");

        try (PrintWriter pw = new PrintWriter(file)) {
            StringBuilder sb = new StringBuilder();

            sb.append("id,");
            sb.append("first_name,");
            sb.append("last_name,");
            sb.append("degree");
            sb.append("\n");

            for(List<String> list : values) {
                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i));
                    if (i < list.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append('\n');
            }

            pw.write(sb.toString());
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
