package edu.virginia.cs.reviews;

public class Student {
    private String name;
    private String password;

    public Student (String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
