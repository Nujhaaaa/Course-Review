package edu.virginia.cs.reviews;

public class Main {
    public static void main(String[] args) {
        System.out.println("Make sure you updated the build.gradle " +
                "file to point to the right main class");
        DatabaseImpl database = new DatabaseImpl();
        database.connect();
        database.addStudent("Anushka", "password");
    }
}
