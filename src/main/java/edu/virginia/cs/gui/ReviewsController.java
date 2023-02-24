package edu.virginia.cs.gui;

import edu.virginia.cs.reviews.DatabaseImpl;
import edu.virginia.cs.reviews.Reviews;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;


public class ReviewsController {
    public Label reviewsLabel;
    @FXML
    public VBox scene;
    @FXML
    public VBox vBox;
    @FXML
    public VBox menu;

    @FXML
    public Button loginButton;

    @FXML
    public Button logoutButton;
    @FXML
    public Button newLoginButton;
    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public TextField reenterPassword;
    @FXML
    public Button enter;
    @FXML
    public Label error;
    public String userInput;
    public String passInput;
    public String reenterInput;
    public boolean newLogin = false;

    @FXML
    public Button submit;
    @FXML
    public Button view;
    @FXML
    public TextField course;
    @FXML
    public Label courseLabel;
    @FXML
    public Button courseSubmit;
    public boolean submitCourse = false;
    public String mnemonic;
    public String courseNumber;
    @FXML
    public Label invalidCourse;
    public String courseText;
    @FXML
    public Label courseMenuLabel;
    @FXML
    public TextField review;
    @FXML
    public ComboBox<Integer> ratingsCombo;
    @FXML
    public VBox reviews;
    @FXML
    public Button submitReview;
    public int rating = 0;
    @FXML
    public Label ratingError;
    public DatabaseImpl database;

    @FXML
    public VBox viewReviews;
    @FXML
    public Label reviewsList;

    @FXML
    public Label reviewMessage;
    public boolean viewReview;
    public void initialize() {
        ratingsCombo.getItems().setAll(1, 2, 3, 4, 5);
        database = new DatabaseImpl();
        database.connect();
    }


    @FXML
    public void login() {
        initiateLoginPage();
    }

    public void initiateLoginPage() {
        enter.setVisible(true);
        loginButton.setVisible(false);
        newLoginButton.setVisible(false);
        username.setVisible(true);
        password.setVisible(true);
        error.setVisible(false);
    }

    public void startInitialPage() {
        enter.setVisible(false);
        username.setVisible(false);
        password.setVisible(false);
        reenterPassword.setVisible(false);
        loginButton.setVisible(true);
        newLoginButton.setVisible(true);
        username.clear();
        password.clear();
        reenterPassword.clear();
    }

    public void logOutPage() {
        menu.setVisible(false);
        reviews.setVisible(false);
        vBox.setVisible(true);
        loginButton.setVisible(true);
        newLoginButton.setVisible(true);
        username.clear();
        password.clear();
        reenterPassword.clear();
        newLogin = false;
    }

    @FXML
    public void newLogin() {
        reenterPassword.setVisible(true);
        newLogin = true;
        initiateLoginPage();
    }

    @FXML
    public void logOut() {
        resetOrder();
        logOutPage();
    }


    public void disableLoginPage() {
        username.setVisible(false);
        password.setVisible(false);
        reenterPassword.setVisible(false);
        enter.setVisible(false);
    }

    public void loginErrors(String message) {
        error.setText(message);
        error.setVisible(true);
        disableLoginPage();
        startInitialPage();
    }

    public void loginSuccessful() {
        boolean result = checkCredentials(userInput, passInput, reenterInput);
        if (result) {
            disableLoginPage();
            initiateMainMenu();
        }
        logoutButton.setVisible(true);
    }


    @FXML
    public void enterCredentials() {
        userInput = username.getText();
        passInput = password.getText();
        reenterInput = reenterPassword.getText();

        if (reenterInput.equals("")) {
            try {
                if (database.loginSuccessful(userInput, passInput)) {
                    loginSuccessful();
                } else {
                    loginErrors("Login Failed");
                }
            } catch (SQLException e) {
                loginErrors("Login Failed");
            }
        } else {
            try {
                database.signUp(userInput, passInput, reenterInput);
                loginSuccessful();
            } catch (IllegalStateException e) {
                loginErrors("Passwords do not match");
            } catch (IllegalArgumentException e) {
                loginErrors("User already exists. Login instead.");
            } catch (RuntimeException e) {
                loginErrors("Username already exists. Enter a different username or Login instead");
            } catch (SQLException e) {
                loginErrors("Username already exists. Enter a different username or Login instead");
            }
        }


    }

    public void initiateMainMenu() {
        menu.setVisible(true);
        submit.setVisible(true);
        view.setVisible(true);
        reviews.setVisible(false);
        courseLabel.setVisible(false);
        course.setVisible(false);
        courseSubmit.setVisible(false);
    }

    public boolean checkCredentials(String user, String pass, String reenter) {
        if (user.equals("") && pass.equals("")) {
            error.setVisible(true);
            error.setText("Enter Username & Password");
            return false;
        } else if (user.equals("")) {
            error.setVisible(true);
            error.setText("Enter Username");
            return false;
        } else if (pass.equals("")) {
            error.setText("Enter Password");
            error.setVisible(true);
            return false;
        } else if (reenter.equals("") && newLogin) {
            error.setText("Re-enter Password");
            error.setVisible(true);
            return false;
        } else {
            error.setVisible(false);
        }
        return true;
    }

    @FXML
    public void submitReview() {
        submitCourse = true;
        invalidCourse.setVisible(false);
        handleReviewButtons();
    }

    @FXML
    public void viewReviews() {
        submitCourse = false;
        handleReviewButtons();
    }

    public void handleReviewButtons() {
        submit.setVisible(false);
        view.setVisible(false);
        course.setVisible(true);
        courseLabel.setVisible(true);
        courseSubmit.setVisible(true);
    }

    public void menuFixes() {
        courseSubmit.setVisible(false);
        courseLabel.setVisible(false);
        course.setVisible(false);
    }

    @FXML
    public void handleEnterCourse() {
        courseText = course.getText();
        String[] courses = courseText.split(" ");
        if (courses.length != 2) {
            invalidCourse.setVisible(true);
        } else {
            mnemonic = courses[0];
            courseNumber = courses[1];
            //checkValidCourseName returns false if course is valid
            if (!checkValidCourseName() && submitCourse) {
                reorderSubmitChildren();
                validCourseRequest();
            } else if (!checkValidCourseName()){
                viewCourseReviews(mnemonic, courseNumber);
            }
        }
        course.clear();
    }

    public void resetOrder() {
        scene.getChildren().clear();
        scene.getChildren().add(reviewsLabel);
        scene.getChildren().add(logoutButton);
        scene.getChildren().add(vBox);
        scene.getChildren().add(menu);
        scene.getChildren().add(reviews);
        scene.getChildren().add(viewReviews);
    }

    public void reorderChildren() {
        scene.getChildren().clear();
        scene.getChildren().add(viewReviews);
        scene.getChildren().add(logoutButton);
        scene.getChildren().add(reviews);
        scene.getChildren().add(menu);
        scene.getChildren().add(vBox);
    }

    public void reorderSubmitChildren() {
        scene.getChildren().clear();
        scene.getChildren().add(reviews);
        scene.getChildren().add(logoutButton);
        scene.getChildren().add(viewReviews);
        scene.getChildren().add(menu);
        scene.getChildren().add(vBox);
    }

    public void viewCourseReviews(String mnemonic, String courseNumber) {
        courseSubmit.setVisible(false);
        invalidCourse.setVisible(false);
        courseMenuLabel.setText(courseText);
        courseMenuLabel.setVisible(true);
        course.setVisible(false);
        courseLabel.setVisible(false);

        ArrayList<Reviews> listRevs= database.getReviews(mnemonic, courseNumber);
        double avgRev = 0.0;
        String outputRev;
        if(listRevs.size()>0){
            for(int i = 0; i <listRevs.size(); i++){
                avgRev+=listRevs.get(i).getRating();
            }
            avgRev=avgRev/(listRevs.size());
            //display "The average review score is" + avgRev then list the reviews
            outputRev = "Average Review Score (out of 5): "+Double.toString(avgRev);
            reviewsList.setText(outputRev);

            outputRev = "";
            for(int i =0; i<listRevs.size();i++){
//                outputRev = listRevs.get(i).getMessage() + outputRev + "<br>" ;
                if (!viewReview) {
                    Label label = new Label(listRevs.get(i).getMessage());
                    label.setStyle("-fx-text-fill: #EEEEE4");
                    viewReviews.getChildren().add(label);
                }
            }
            reorderChildren();
            viewReviews.setVisible(true);
            reviewMessage.setText(outputRev);
            reviewMessage.setVisible(true);
            viewReview = true;
            //System.out.println(outputRev);
        }
        else{
            invalidCourse.setText("No reviews have been submitted for this course");
            invalidCourse.setVisible(true);
            initiateMainMenu();
        }
    }

    public void validCourseRequest() {
        if (database.checkIfStudentReviewed(mnemonic, courseNumber, userInput)) {
            studentReviewedCheck();
        } else {
            courseSubmit.setVisible(false);
            invalidCourse.setVisible(false);
            courseMenuLabel.setText(courseText);
            courseMenuLabel.setVisible(true);
            if (submitCourse) {
                reviews.setVisible(true);
            } else {
                viewCourseReviews(mnemonic, courseNumber);
            }
            menuFixes();
        }
        resetOrder();
    }

    public void studentReviewedCheck() {
        invalidCourse.setText("You have already reviewed this course");
        invalidCourse.setVisible(true);
        submit.setVisible(true);
        view.setVisible(true);
        menuFixes();
    }

    private boolean checkValidCourseName() {
        boolean result = database.checkValidCourse(mnemonic, courseNumber);
        if (result) {
            invalidCourse.setText("Invalid Course");
            invalidCourse.setVisible(true);
            initiateMainMenu();
        }
        return result;
    }

    public void addReview() {
        //https://stackoverflow.com/questions/21921505/combobox-with-integer-values referenced to implement comboBox
        resetOrder();
        if (ratingsCombo.getValue() == null) {
            ratingError.setVisible(true);
            ratingError.setText("Rate the Course");
        } else if (review.getText().length() == 0) {
            ratingError.setVisible(true);
            ratingError.setText("Enter Message for Course Review");
        } else {
            rating = ratingsCombo.getValue();
            String message = review.getText();
            ratingsCombo.setValue(null);
            review.clear();
            ratingError.setVisible(false);
            reviews.setVisible(false);
            initiateMainMenu();
            handleSubmitReview(message);
        }
    }

    public void handleSubmitReview(String message) {
        try {
            database.submitReview(mnemonic, courseNumber, userInput, message, rating);
            reorderSubmitChildren();
        } catch (IllegalStateException e) {
            ratingError.setText("You have already reviewed this course");
        }
    }

    public void returnMainMenu() {
        initiateMainMenu();
        resetOrder();
        viewReviews.setVisible(false);
    }


}
