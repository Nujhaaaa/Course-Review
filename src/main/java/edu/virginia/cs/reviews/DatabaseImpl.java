package edu.virginia.cs.reviews;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseImpl {
    private static final String REVIEWS_PATH = "Reviews.sqlite3";
    private Scanner scanner;

    private Connection connection;

    public void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + REVIEWS_PATH);
                if (!tableExist("Students") || !tableExist("Courses") || !tableExist("Reviews")) {
                    createTables();
                }
            } else {
                throw new IllegalStateException("Manager is connected");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tableExist(String table) {
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("No connection");
            }
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, table, null);
            return tables.next();
        } catch (SQLException e) {
            return false;
        }
    }


    public void createTables() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager has not been connected");
            }

            Statement s = connection.createStatement();

            if (!tableExist("Students")) {
                String students = "CREATE TABLE Students (" +
                        "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "Name varchar(100) NOT NULL UNIQUE," +
                        "Password varchar(100) NOT NULL" +
                        ");";
                s.executeUpdate(students);
                s.close();
            }

            if (!tableExist("Courses")) {
                String courses = "CREATE TABLE Courses (" +
                        "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "Department varchar(100) NOT NULL," +
                        "Catalog_Number varchar(100) NOT NULL" +
                        ");";
                s.executeUpdate(courses);
                s.close();
            }

            if (!tableExist("Reviews")) {
                String reviews = "CREATE TABLE Reviews (" +
                        "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "StudentID INTEGER NOT NUll," +
                        "CourseID INTEGER NOT NULL," +
                        "Review varchar(100) NOT NULL," +
                        "Rating int NOT NULL," +
                        "FOREIGN KEY (StudentID) REFERENCES Students(ID) ON DELETE CASCADE," +
                        "FOREIGN KEY (CourseID) REFERENCES Courses(ID) ON DELETE CASCADE," +
                        "CHECK (1 <= Rating <= 5)" +
                        ");";
                s.executeUpdate(reviews);
                s.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addStudent(String user, String pass) {
        try {
            Statement statement = connection.createStatement();
            String query = String.format("""
                insert into Students (name, password) values("%s", "%s");
                """, user, pass);
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void submitReview(String mnemonic, String number, String studentName, String message, int rating) {
        try {
            if (connection.isClosed() || connection == null) {
                throw new IllegalStateException("Connection is closed right.");
            }
            Statement statement = connection.createStatement();

            if (checkIfCourseAdded(mnemonic, number)) {
                String insertQuery = String.format("""
                        insert into Courses (department, catalog_number) values("%s", "%s");
                        """, mnemonic, number);
                statement.executeUpdate(insertQuery);
                statement.close();

                int studentId = getStudentIDFromName(studentName);
                int courseId = getCourseIDFromName(mnemonic, number);
                String reviewQuery = String.format("""
                        insert into Reviews (studentId, courseId, review, rating) values("%d", "%d", "%s", "%d");
                        """, studentId, courseId, message, rating);
                statement.executeUpdate(reviewQuery);
                statement.close();

            } else if (!checkIfStudentReviewed(mnemonic, number, studentName)) {
                int studentId = getStudentIDFromName(studentName);
                int courseId = getCourseIDFromName(mnemonic, number);
                String reviewQuery = String.format("""
                        insert into Reviews (studentId, courseId, review, rating) values("%d", "%d", "%s", "%d");
                        """, studentId, courseId, message, rating);
                statement.executeUpdate(reviewQuery);
                statement.close();
            } else {
                throw new IllegalStateException("Student has already reviewed the course");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Reviews> getReviews(String mnemonic, String courseNumber) {
        try {
            if (connection.isClosed() || connection == null) {
                throw new IllegalStateException("Connection is closed.");
            }
            Statement statement = connection.createStatement();
            ArrayList<Reviews> reviewList = new ArrayList<Reviews>();
            if (!checkIfCourseAdded(mnemonic, courseNumber)) {
                int courseId = getCourseIDFromName(mnemonic, courseNumber);
                ResultSet rs = statement.executeQuery("SELECT * FROM REVIEWS WHERE COURSEID = " + courseId);
                while (rs.next()) {
                    String msg = rs.getString("Review");
                    int rating = rs.getInt("Rating");
                    Reviews rev = new Reviews(msg, rating);
                    reviewList.add(rev);
                }
                statement.close();
            }
            return reviewList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkValidCourse(String mnemonic, String number) {
        boolean invalid = false;

        for (Character c : mnemonic.toCharArray()) {
            if (Character.isLowerCase(c)) {
                invalid = true;
            }
        }

        if (mnemonic.length() > 4) {
            invalid = true;
        }

        for (Character c : number.toCharArray()) {
            if (!Character.isDigit(c)) {
                invalid = true;
            }
        }
        return invalid;
    }


    public int getStudentIDFromName(String name) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Students;";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                if (rs.getString("Name").equals(name)) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public int getCourseIDFromName(String mnemonic, String number) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Courses;";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                if (rs.getString("Catalog_Number").equals(number) && rs.getString("Department").equals(mnemonic)) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public boolean checkIfStudentReviewed(String mnemonic, String number, String studentName) {
        try {
            Statement statement = connection.createStatement();
            int courseID = getCourseIDFromName(mnemonic, number);

            if (courseID == -1) {
                return false;
            }

            ResultSet rs = statement.executeQuery("SELECT * FROM REVIEWS WHERE COURSEID = " + courseID);

            while (rs.next()) {
                    int studentId = getStudentIDFromName(studentName);
                    if(rs.getInt("studentID") == studentId) {
                        return true;
                    }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfCourseAdded(String mnemonic, String number) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Courses;";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                if (rs.getString("Department").equals(mnemonic) && rs.getString("Catalog_Number").equals(number)) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            return true;
        }
    }


    private String prompt(String s) {
        scanner = new Scanner(System.in);
        System.out.print(s);
        return scanner.nextLine();
    }

    public boolean loginSuccessful(String username, String passWord) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Students;";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                if (rs.getString("Name").equals(username)) {
                    String name = rs.getString("Name");
                    String pass = rs.getString("Password");
                    return (name.equals(username) && pass.equals(passWord));
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public void login() throws SQLException{
        String username = prompt("Enter username: ");
        String passWord = prompt("Enter password: ");
        if(loginSuccessful(username, passWord)){
            System.out.println("Login Successful");
        }
        else{                                     //how to go to right screen if login valid or invalid?
            System.out.println("Login failed, try again!");
        }

    }

        public void signUp(String user, String pass, String reenter) throws  SQLException{
            if(!pass.equals(reenter)){
                throw new IllegalStateException("Passwords do not match");
            }
            else{                                                   //how to go to right page if login valid or invalid??
                try {
                    addStudent(user, pass);
                }catch(IllegalArgumentException e){
                    throw new IllegalArgumentException("User already exists. Login instead.");
                }
            }
        }

}
