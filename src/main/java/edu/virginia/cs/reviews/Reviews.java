package edu.virginia.cs.reviews;

public class Reviews {
    private String message;
    private int rating;

    public Reviews (String message, int rating) {
        this.message = message;
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public int getRating() {
        return rating;
    }

}
