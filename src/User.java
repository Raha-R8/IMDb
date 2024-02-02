import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;
import javafx.geometry.Pos;
import org.omg.PortableInterceptor.ServerRequestInfo;
import sun.nio.ch.sctp.SctpNet;
import sun.rmi.server.InactiveGroupException;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.event.AdjustmentEvent;
import java.awt.image.AreaAveragingScaleFilter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.Serializable;

public class User extends Person {
    private String userName;
    private String password;
    private LocalDate accountRegistration;
    private UserType userType;

    private Map<String, FilmList> filmLists;
    private ArrayList<Rate> rates;
    private ArrayList<Edit> edits;
    private ArrayList<Post> posts;
    private HashMap<User, ArrayList<Post>> direct;
    private HashMap<String, Group> groups;


    //****************************************
    //getters and setters


    public HashMap<User, ArrayList<Post>> getDirect() {
        return direct;
    }

    public void setDirect(HashMap<User, ArrayList<Post>> direct) {
        this.direct = direct;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public HashMap<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, Group> groups) {
        this.groups = groups;
    }

    public ArrayList<Edit> getEdits() {
        return edits;
    }

    public void setEdits(ArrayList<Edit> edits) {
        this.edits = edits;
    }

    public ArrayList<Rate> getRates() {
        return rates;
    }

//    public void setRates(ArrayList<Rate> rates) {
//        this.rates = rates;
//    }


    public Map<String, FilmList> getFilmLists() {
        return filmLists;
    }

    public void setFilmLists(Map<String, FilmList> filmLists) {
        this.filmLists = filmLists;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setUserType(UserType userType) {
        this.userType = userType;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getAccountRegistration() {
        return accountRegistration;
    }

    public UserType getUserType() {
        return userType;
    }


//*********************************************************************
    //constructor

    private User(String userName, String password, UserType userType) {
        // super( name,  lastname,  age,  nationalID,  geoLoc,  addressDetail,  gender,  following);
        this.userName = userName;
        this.password = password;
        this.accountRegistration = LocalDate.now();
        this.userType = userType;
        this.filmLists = new HashMap<>();
        FilmList watchList = new FilmList("favorites", ListType.WATCHLIST, LocalDate.now(), new HashMap<String, Movie>());
        FilmList classicsToSee = new FilmList("classicsToSee", ListType.CLASSICSTOSEE, LocalDate.now(), new HashMap<String, Movie>());
        this.filmLists.put("favorites", watchList);
        this.filmLists.put("classicsToSee", classicsToSee);
        this.rates = new ArrayList<>();
        this.edits = new ArrayList<>();
        //making sure that if the account changes or there needs to be things added to these fields they wouldn't be null
        this.setFollowing(new ArrayList<>());
        this.setActingRecords(new HashMap<>());
        this.setFollowers(new ArrayList<>());
        //making sure group forwards and posts exist
        this.posts = new ArrayList<>();
        this.direct = new HashMap<>();
        this.groups = new HashMap<>();
    }

    //*********************************************************************
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Year isYear(String s) {
        try {
            Year year = Year.parse(s);
            return year;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    //Database
    //I brought them here because now I can make them private and don't allow access to database from other classes

    private void addUserToDB() {
        DataBase.getUserMap().put(this.getUserName(), this);
    }

    // Authenticate a user based on username and password
    private static User searchUserInDB(String userName) {
        return DataBase.getUserMap().get(userName);
    }

    // Helper method to generate a key from username and password


    private void deleteUserFromDB() {
        DataBase.getUserMap().remove(this.getUserName(), this);
    }

    //**********************************************************************************************
    //other methods
    public static User makeFirstAdmin() {
        User user = new User("admin", "admin", UserType.ADMIN);
        user.addUserToDB();
        return user;
    }

    public static User signUp(String username, String password) {
        if (User.searchUserInDB(username) == null) {
            User user = new User(username, password, UserType.NORMALACCOUNT);
            user.addUserToDB();
            return user;
        } else {
            //returns null if a user with that username already exists
            return null;
        }
    }

    public static User signIn(String username, String password) {
        User user;
        if ((user = User.searchUserInDB(username)) != null) {
            if (!user.getPassword().equals(password)) {
                return null;
            }
            //this is just for now but it will be replaced by the things that would be
            //displayed on a users homepage
            return user;
        } else {
            return null;
        }
    }

    //issue:
    //how can I reduce the usage of this method because I want it to be used in set official account method but not of its own record
    public void setPersonData(String name, String lastname, int age, String nationalID, Gender gender, Map<String, ActingRecord> actingRecords, ArrayList<Person> following) {
        this.setName(name);
        this.setLastname(lastname);
        this.setAge(age);
        this.setNationalID(nationalID);
        this.setGender(gender);
        this.setActingRecords(actingRecords);
        this.setFollowing(following);
    }

    public int makeAdminOrEditor(UserType userType, User admin, String name, String lastname, int age, String nationalID, Gender gender) {
        if (admin.userType == UserType.ADMIN) {
            this.setPersonData(name, lastname, age, nationalID, gender, this.getActingRecords(), this.getFollowing());//users that are admin must have all their personal data set
            this.userType = userType;
            return 1;
        }
        return 0;
    }


    public int changePassword(String previousPass, String newPass) {
        if (this.password.equals(previousPass)) {
            this.password = newPass;
            return 1;
        }
        return 0;
    }

    public int changeUsername(String password, String newUsername) {
        if (this.password.equals(password)) {
            this.deleteUserFromDB();
            this.userName = newUsername;
            this.addUserToDB();
            return 1;
        }
        return 0;
    }

    //I might want to change this to either user or person as following but for now it is only person
    //_____________________________________________________      following       ____________________________________________________________
    public int addFollowing(Person person) {
        if (!this.getFollowing().contains(person)) {
            this.getFollowing().add(person);
            person.getFollowers().add(this);
            return 1;
        }
        return 0;
    }

    public HashMap<Integer, Person> showFollowings() {
        int i = 1;
        HashMap<Integer, Person> followingsMap = new HashMap<>();
        ArrayList<Person> followings = this.getFollowing();
        if (followings.isEmpty()) {
            return null;
        }
        for (Person person : followings) {
            if (person instanceof User) {
                System.out.printf("%d. %s\n", i, ((User) person).getUserName());
            } else System.out.printf("%d. %s\n", i, person.getName() + ' ' + person.getLastname());
            followingsMap.put(i, person);
            i++;
        }
        return followingsMap;
    }

    public void followingFunctionality() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equals("0")) {
            System.out.println("0. Exit follow menu");
            System.out.println("1. Show following list and choose a user to view their account info");
            System.out.println("2. Follow a user");
            System.out.println("3. Follow a Person");
            System.out.println("4. Show Following list of a user and choose a user in their followings to follow if you want to");

            choice = scanner.nextLine();
            String username;
            User user;
            HashMap<Integer, Person> peopleMap;
            Person person = null;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    peopleMap = this.showFollowings();
                    if (peopleMap == null) {
                        System.out.println("This list is empty");
                        break;
                    }
                    System.out.println("Enter the number of the user you want to see more about");
                    if (!isInteger(choice = scanner.nextLine())) {
                        System.out.println("The string you entered is not a number");
                        break;
                    }
                    person = peopleMap.get(Integer.parseInt(choice));
                    if (person == null) {
                        System.out.println("The number you entered does not match any person on the list");
                        break;
                    }
                    if (person instanceof User) {
                        ((User) person).showAllUserData();
                    } else
                        person.viewPerson();
                    break;
                case "2":
                    while (true) {
                        System.out.println("Enter 1 to exit this menu and any other character to proceed");
                        choice = scanner.nextLine();
                        if (choice.equals("1")) break;
                        System.out.println("Enter the username of the user you want to follow");
                        username = scanner.nextLine();
                        user = DataBase.getUserMap().get(username);
                        if (user == null) {
                            System.out.println("This user does not exist");
                            break;
                        }
                        this.addFollowing(user);
                        System.out.println("User successfully added to followings");
                    }
                    break;
                case "3":
                    peopleMap = Person.showPeople();
                    if (peopleMap.isEmpty()) {
                        System.out.println("This list is empty");
                        break;
                    }
                    System.out.println("Choose the number of the person you want to follow");
                    if (isInteger(choice = scanner.nextLine())) {
                        person = peopleMap.get(Integer.parseInt(choice));
                        if (person == null) {
                            System.out.println("The number you entered does not match any person in the list");
                            break;
                        }
                        int output = this.addFollowing(person);
                        if (output == 0) {
                            System.out.println("You already follow this person");
                            break;
                        }
                        System.out.println("Person successfully added to followings");
                    } else System.out.println("The character you entered is not valid");
                    break;
                case "4":
                    System.out.println("Enter 1 to exit this menu and any other character to proceed");
                    choice = scanner.nextLine();
                    if (choice.equals("1")) break;
                    System.out.println("Enter the username of the user whom you want to see their following list");
                    username = scanner.nextLine();
                    if ((user = User.searchUserInDB(username)) == null) {
                        System.out.println("User with this username does not exist");
                        break;
                    }
                    peopleMap = user.showFollowings();
                    if (peopleMap.isEmpty()) {
                        System.out.println("This list is empty");
                        break;
                    }
                    System.out.println("Enter 1 if you want to exit and any other character to choose one of the people in this list to follow");
                    choice = scanner.nextLine();
                    if (choice.equals("1")) {
                        System.out.println("Choose the number of the person/User you want to follow");
                        if (isInteger(choice = scanner.nextLine())) {
                            person = peopleMap.get(Integer.parseInt(choice));
                            if (person == null) {
                                System.out.println("The number you entered does not match any person/User in the list");
                                break;
                            }
                            int output = this.addFollowing(person);
                            if (output == 0) {
                                System.out.println("You already follow this person");
                                break;
                            }
                            System.out.println("Person/User successfully added to followings");
                        } else System.out.println("The character you entered is not valid");
                    }
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }
    //_____________________________________________________      review       ____________________________________________________________
    //review functionality should be used wherever user has access and can choose a review

    public void reviewFunctionality(Movie movie) {
        Scanner scanner = new Scanner(System.in);
        String option = "";
        int movieReviewNumber = movie.showReviews();
        while (!(option.equals("0"))) {
            System.out.println("0. Exit reviews");
            System.out.println("1. Add review");
            System.out.println("2. Show reviews of the movie, Reply to a review, rate review or flag a review as inappropriate content");
            option = scanner.nextLine();
            String test;
            int choice;
            Review review = null;
            String content;
            Boolean isSpoiler = false;
            switch (option) {
                case "1":
                    //content
                    System.out.println("Enter the content of your review");
                    content = scanner.nextLine();
                    //spoiler check
                    System.out.println("Enter 1 to mark your review as spoiler and 2 if it is not a spoiler");
                    test = scanner.nextLine();
                    while (!((test.equals("1")) || (test.equals("2"))))
                        System.out.println("The character you entered is not a valid option");
                    if (test.equals("1"))
                        isSpoiler = true;

                    //adding a new review that is not a reply to any other reviews in the movie
                    movie.addReview(null, this, content, isSpoiler);
                    System.out.println("Your review was added successfully");
                    break;
                case "2":
                    int movieReviews = movie.showReviews();
                    if (movieReviews == 1) {
                        System.out.println("This list is empty");
                        break;
                    }
                    System.out.println("Choose the number of the review that you wish to see more about; or enter 0 to exit");
                    while (!(test = scanner.nextLine()).equals("0")) {
                        if (isInteger(test)) {
                            choice = Integer.parseInt(test);
                            if (choice > movieReviewNumber) {
                                System.out.println("There is not a review with this number. Enter your choice again or enter 0 to exit");
                                continue;
                            }
                            //choosing the review that user wants to reply to
                            review = movie.getReviews().get(choice - 1);
                            if (review == null) {
                                System.out.println("The number uou entered does not match any review on the list");
                                break;
                            }
                            while (!option.equals("0")) {
                                System.out.println("0. Exit this menu");
                                System.out.println("1. Reply to the review");
                                System.out.println("2. Rate review");
                                System.out.println("3. Flag review as inappropriate content");
                                System.out.println("4. View review replies");
                                option = scanner.nextLine();
                                switch (option) {
                                    case "0":
                                        break;
                                    case "1":
                                        System.out.println("Enter the content of your reply");
                                        content = scanner.nextLine();

                                        //spoiler check
                                        System.out.println("Enter 1 to mark your reply as spoiler and 2 if it is not a spoiler");
                                        while (!((test = scanner.nextLine()).equals("1")) && !(test.equals("2")))
                                            System.out.println("The character you entered is not a valid option");
                                        if (test.equals("1"))
                                            isSpoiler = true;
                                        //adding a new review that is not a reply to any other reviews in the movie
                                        movie.addReview(review, this, content, isSpoiler);
                                        break;
                                    case "2":
                                        int output;
                                        System.out.println("1. Is helpful");
                                        System.out.println("2. Is not helpful");
                                        if ((isInteger(option = scanner.nextLine()))) {
                                            choice = Integer.parseInt(option);
                                            if (choice == 1) {
                                                output = review.rateReview(this, true);
                                                if (output == 0)
                                                    System.out.println("You can't rate this review because you have already rated it.");
                                            } else if (choice == 2) {
                                                output = review.rateReview(this, false);
                                                if (output == 0)
                                                    System.out.println("You can't rate this review because you have already rated it.");
                                            } else
                                                System.out.println("The number you entered does not match any options on the list");
                                        } else System.out.println("The string you entered is not a number");
                                        break;
                                    case "3":
                                        ReviewEdit.addReviewEdit(this, "", "", review);
                                        System.out.println("Review successfully flagged as inappropriate content");
                                        break;
                                    case "4":
                                        review.showReplies();
                                        break;
                                    default:
                                        System.out.println("The character you entered is not a valid option");
                                }

                            }

                        } else System.out.println("The character you entered is not an option");
                    }
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    //_____________________________________________________      rate       ____________________________________________________________
    //only admins are permitted to actually put person information for an account
    //because the information is not needed unless there is a celebrity that wants to have a specific account with his/her personal info
    public Rate searchRates(Movie movie) {
        ArrayList<Rate> rates = this.getRates();
        for (Rate rate : rates) {
            if (rate.getMovie() == movie) {
                return rate;
            }
        }
        return null;
    }

    public HashMap<Integer, Movie> showRatedMovies() {
        int i = 1;
        HashMap<Integer, Movie> ratedMoviesHashmap = new HashMap<>();
        ArrayList<Rate> rates = this.getRates();
        if (rates.isEmpty()) {
            System.out.println("This list is empty");
            return null;
        }
        for (Rate rate : rates) {
            System.out.printf("%d. %s", i, rate.getMovie().getTitle());
            ratedMoviesHashmap.put(i, rate.getMovie());
            i++;
        }
        return ratedMoviesHashmap;
    }

    //_____________________________________________________      edit      ____________________________________________________________
    public static ArrayList<String> chooseEditDetail(String targetObject) {
        ArrayList<String> editTargets = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String choice = " ";
        System.out.println("choose the detail you want to edit");
        String test;
        int option;
        switch (targetObject) {
            case "person":
                System.out.println("1. name");
                System.out.println("2. last name");
                System.out.println("3. age");
                System.out.println("4. nationalID");
                System.out.println("5. gender");
                choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        editTargets.add("name");
                        System.out.println("Enter new name");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "2":
                        editTargets.add("lastname");
                        System.out.println("Enter new last name");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "3":
                        editTargets.add("age");
                        System.out.println("Enter new age");
                        if (isInteger(test = scanner.nextLine()))
                            editTargets.add(test);
                        else {
                            System.out.println("You entered invalid input for age");
                            break;
                        }
                        break;
                    case "4":
                        editTargets.add("nationalID");
                        System.out.println("Enter new national ID");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "5":
                        editTargets.add("gender");
                        System.out.println("choose the new gender");
                        System.out.println("1. Male");
                        System.out.println("2. Female");
                        System.out.println("3. Other");
                        test = scanner.nextLine();
                        switch (test) {
                            case "1":
                                editTargets.add("male");
                                break;
                            case "2":
                                editTargets.add("female");
                                break;
                            case "3":
                                editTargets.add("other");
                                break;
                            default:
                                System.out.println("The character you entered does not match any gender");
                                return null;
                        }
                        break;
                    default:
                        System.out.println("The character you entered is not a valid option");
                        return null;
                }
                break;
            case "movie":
                System.out.println("1. title");
                System.out.println("2. picture path");
                System.out.println("3. trailer path");
                System.out.println("4. movie description");
                System.out.println("5. budget");
                System.out.println("6. release date");
                System.out.println("7. language");
                choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        editTargets.add("title");
                        System.out.println("Enter new title");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "2":
                        editTargets.add("picturePath");
                        System.out.println("Enter new picture path");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "3":
                        editTargets.add("trailerPath");
                        System.out.println("Enter new trailer path");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "4":
                        editTargets.add("movieDescription");
                        System.out.println("Enter new movie description");
                        editTargets.add(scanner.nextLine());
                        break;
                    case "5":
                        editTargets.add("budget");
                        System.out.println("Enter new budget");
                        if (isInteger(test = scanner.nextLine()))
                            editTargets.add(test);
                        else {
                            System.out.println("You entered invalid input");
                            break;
                        }
                        break;
                    case "6":
                        editTargets.add("releaseDate");
                        try {
                            System.out.print("Enter new release date (yyyy-MM-dd): ");
                            // Parse the user input into a LocalDate object
                            LocalDate.parse(test = scanner.nextLine());
                        } catch (Exception e) {
                            // Handle the case where the user enters an invalid date format
                            System.out.println("Invalid date format. Please enter a date in the format yyyy-MM-dd.");
                            return null;
                        }
                        editTargets.add(test);
                        break;
                    case "7":
                        editTargets.add("language");
                        System.out.println("Enter new language");
                        editTargets.add(scanner.nextLine());
                        break;
                    default:
                        System.out.println("The character you entered is not a valid option");
                        return null;
                }
                break;

        }
        return editTargets;
    }

    public void publishEdit() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equals("0")) {
            Edit edit;
            ArrayList<String> detailToEdit;
            System.out.println("0. Exit publish edit menu");
            System.out.println("1. Publish edit for a person biography");
            System.out.println("2. Publish edit for a movie information");
            System.out.println("3. Flag a review as inappropriate content");
            choice = scanner.nextLine();
            Movie movie;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    Person person;
                    String nationalID;
                    System.out.println("Enter the nationalID of the person you want to add bio edit for");
                    nationalID = scanner.nextLine();
                    person = DataBase.getPeopleMap().get(nationalID);
                    if (person == null) {
                        System.out.println("The nationalID you entered is not for any person");
                        break;
                    }
                    //another method to choose between details and give one back
                    detailToEdit = chooseEditDetail("person");
                    if (detailToEdit == null) {
                        System.out.println("You didn't properly choose a detail to edit, exiting edit menu...");
                        return;
                    }
                    BiographyEdit.addBiographyEdit(person, this, detailToEdit.get(0), detailToEdit.get(1));
                    System.out.println("Edit published successfully");
                    break;
                case "2":
                    System.out.println("Enter the name of the movie you want to publish edit for");
                    movie = DataBase.getMovies().get(scanner.nextLine());
                    if (movie == null) {
                        System.out.println("Movie with this name does not exist");
                        return;
                    }
                    detailToEdit = chooseEditDetail("movie");
                    if (detailToEdit == null) {
                        System.out.println("You didn't properly choose a detail to edit, exiting edit menu...");
                        return;
                    }
                    MovieEdit.addMovieEdit(movie, this, detailToEdit.get(0), detailToEdit.get(1));
                    break;
                case "3":
                    System.out.println("Enter the name of the movie you want to publish edit for");
                    movie = DataBase.getMovies().get(scanner.nextLine());
                    if (movie == null) {
                        System.out.println("Movie with this name does not exist");
                        return;
                    }
                    int movieReviewNumber = movie.showReviews();
                    String test;
                    int x;
                    Review review = null;
                    System.out.println("Choose the number of the review that you wish to flag as inappropriate content");
                    if (isInteger(test = scanner.nextLine())) {
                        x = Integer.parseInt(test);
                        if (x > movieReviewNumber) {
                            System.out.println("There is not a review with this number. Enter your choice again or enter 0 to exit");
                            return;
                        }
                        review = movie.getReviews().get(x);
                        if (review == null) {
                            System.out.println("The number you entered does not match any reviews");
                            break;
                        }
                        ReviewEdit.addReviewEdit(this, "", "", review);
                    }
                    System.out.println("Invalid input: You entered a string instead of number");
                    break;

                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    public void adminEditSetting() {
        String choice = "";
        Scanner scanner = new Scanner(System.in);
        while (!choice.equals("0")) {
            System.out.println("0. Exit edit menu");
            System.out.println("1. See biography edits");
            System.out.println("2. See movie edits");
            System.out.println("3. See flagged reviews");
            choice = scanner.nextLine();
            Edit edit = null;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    HashMap<Integer, BiographyEdit> biographyEditHashMap = BiographyEdit.showBiographyEdits();
                    if (biographyEditHashMap == null) {
                        System.out.println("List is empty");
                        break;
                    }
                    System.out.println("Enter the number of the edit you want to confirm or reject");
                    if (isInteger(choice = scanner.nextLine())) ;
                    edit = biographyEditHashMap.get(Integer.parseInt(choice));
                    break;
                case "2":
                    HashMap<Integer, MovieEdit> movieEditHashMap = MovieEdit.showMovieEdits();
                    if (movieEditHashMap == null) {
                        System.out.println("List is empty");
                        break;
                    }
                    System.out.println("Enter the number of the edit you want to confirm or reject");
                    if (isInteger(choice = scanner.nextLine())) ;
                    edit = movieEditHashMap.get(Integer.parseInt(choice));

                    break;
                case "3":
                    HashMap<Integer, ReviewEdit> reviewEditHashMap = ReviewEdit.showReviewEdits();
                    if (reviewEditHashMap == null) {
                        System.out.println("List is empty");
                        break;
                    }
                    System.out.println("Enter the number of the edit you want to confirm or reject");
                    if (isInteger(choice = scanner.nextLine())) ;
                    edit = reviewEditHashMap.get(Integer.parseInt(choice));
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
            if (edit == null) {
                System.out.println("You did not properly choose an edit");
                continue;
            }
            System.out.println("1. Confirm edit");
            System.out.println("2. Reject edit");
            choice = scanner.nextLine();
            if (choice.equals("1"))
                edit.confirmEdit(this);
            else if (choice.equals("2"))
                edit.rejectEdit(this);
            else System.out.println("The character you entered is not a valid option");
        }

    }

    //_____________________________________________________      movie      ____________________________________________________________
    //this method is called movie functionality and it should be used after user has chosen a movie to work with
    //so basically it should be called wherever a user has access to a movie list
    public void movieFunctionality(Movie movie) {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equals("0")) {
            System.out.println("0. Exit menu");
            System.out.println("1. Show movie information");
            System.out.println("2. Show movie cast");
            System.out.println("3. Review menu");
            System.out.println("4. Rate Movie or change previous rate");
            System.out.println("5. Delete your rate from this movie");
            choice = scanner.nextLine();
            int actingRecordNumber;
            Person person;
            int option;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    movie.viewMovie();
                    Rate rate;
                    if ((rate = this.searchRates(movie)) != null) {
                        System.out.printf("Your rate to this movie : %s star\n", rate.getRateInt());
                    } else
                        System.out.println("You haven't rated this movie before");
                    break;
                case "2":
                    System.out.println("Cast: ");
                    actingRecordNumber = movie.showActingRecords();
                    if (actingRecordNumber == 1) {
                        System.out.println("This list is empty");
                        break;
                    }
                    //choose from cast and see that persons info
                    System.out.println("Enter 1 to exit menu and any other character to choose a record to see that persons information");
                    choice = scanner.nextLine();
                    if (choice.equals("1")) break;
                    System.out.println("Enter the number that matches the person you want to see");
                    if (isInteger(choice = scanner.nextLine())) {
                        option = Integer.parseInt(choice) - 1;
                        if (option > actingRecordNumber) {
                            System.out.println("The number you chose does not match any person");
                            break;
                        }
                        person = movie.getActingRecords().get(option).getCrewMember();
                        //view the person info
                        person.viewPerson();
                    }
                    System.out.println("The character you entered is not a valid option");
                    break;
                case "3":
                    //choose from reviews to view or reply or see replies
                    //much is same with admin
                    this.reviewFunctionality(movie);
                    break;
                case "4":
                    System.out.println("1. one star");
                    System.out.println("2. two star");
                    System.out.println("3. three star");
                    System.out.println("4. four star");
                    System.out.println("5. five star");
                    choice = scanner.nextLine();
                    switch (choice) {
                        case "1":
                            movie.rate(this, RateType.ONESTAR);
                            System.out.println("You have successfully rated this movie");
                            break;
                        case "2":
                            movie.rate(this, RateType.TWOSTAR);
                            System.out.println("You have successfully rated this movie");
                            break;
                        case "3":
                            movie.rate(this, RateType.THREESTAR);
                            System.out.println("You have successfully rated this movie");
                            break;
                        case "4":
                            movie.rate(this, RateType.FOURSTAR);
                            System.out.println("You have successfully rated this movie");
                            break;
                        case "5":
                            movie.rate(this, RateType.FIVESTAR);
                            System.out.println("You have successfully rated this movie");
                            break;
                        default:
                            System.out.println("The character you entered is not a valid option");
                    }
                    break;
                case "5":
                    movie.deleteRate(this);
                    System.out.println("Rate removed successfully");
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }

        }
    }

    public ArrayList<Movie> recomendMoviesGenre() {
        ArrayList<Movie> recomendMovies = new ArrayList<>();
        ArrayList<Genre> favoriteGenres = new ArrayList<>();
        Collection<Movie> favoriteMovies = this.filmLists.get("favorites").getMovies().values();

        for (Movie movie : favoriteMovies) {
            for (Genre genre : movie.getGenres()) {
                favoriteGenres.add(genre);
            }
        }
        recomendMovies = Movie.findMoviesWithSpecificGenres(favoriteGenres);
        return recomendMovies;
    }

    public ArrayList<Movie> recomendMoviesLanguage() {
        ArrayList<Movie> recomendMovies = new ArrayList<>();
        ArrayList<Language> favoriteLanguages = new ArrayList<>();
        Collection<Movie> allMovies = DataBase.getMovieRating();
        Collection<Movie> favoriteMovies = this.filmLists.get("favorites").getMovies().values();

        for (Movie movie : favoriteMovies) {
            favoriteLanguages.add(movie.getLanguage());
        }
        for (Language language : favoriteLanguages) {
            for (Movie movie : allMovies) {
                if (movie.getLanguage() == language) {
                    recomendMovies.add(movie);
                }
            }
        }
        return recomendMovies;
    }

    public ArrayList<Movie> recomendMoviesCast() {
        ArrayList<Movie> recomendMovies = new ArrayList<>();
        ArrayList<Person> favoriteCast = new ArrayList<>();
        Collection<Movie> allMovies = DataBase.getMovieRating();
        Collection<Movie> favoriteMovies = this.filmLists.get("favorites").getMovies().values();
        ;
        int i = 0;
        for (Movie movie : favoriteMovies) {
            for (ActingRecord actingRecord : movie.getActingRecords()) {
                if (actingRecord.getJobOnSet().getOccupation().equalsIgnoreCase("female lead") || actingRecord.getJobOnSet().getOccupation().equalsIgnoreCase("male lead") || actingRecord.getJobOnSet().getOccupation().equalsIgnoreCase("director")) {
                    favoriteCast.add(actingRecord.getCrewMember());
                }
            }
        }
        for (Person person : favoriteCast) {
            for (Movie movie : allMovies) {
                for (ActingRecord actingRecord : movie.getActingRecords()) {
                    if (actingRecord.getCrewMember() == person) {
                        recomendMovies.add(movie);
                    }
                }
            }
        }
        return recomendMovies;
    }

    //_____________________________________________________      film list      ____________________________________________________________
    //returns 0 if there is a list with that name
    //only watch lists can be created by the user
    public int showWatchLists() {
        int i = 1;
        for (String watchlist : this.getFilmLists().keySet()) {
            if ((!watchlist.equals("favorites")) && (!watchlist.equals("classicsToSee"))) {
                System.out.printf("%d. %s\n", i, watchlist);
                i++;
            }
        }
        return i;
    }

    public int createWatchList(String listName) {
        if (this.filmLists.containsKey(listName))
            return 0;
        FilmList filmList = new FilmList(listName, ListType.WATCHLIST, LocalDate.now(), new HashMap<>());
        this.filmLists.put(listName, filmList);
        return 1;
    }

    //  returns 0 if the movie already is in the list
    public int addMovieToWatchList(FilmList filmList, Movie movie) {
        if (filmList.getMovies().get(movie.getTitle()) != null)
            return 0;
        filmList.getMovies().put(movie.getTitle(), movie);
        //adding the film list to the movies film lists
        movie.getFilmLists().add(filmList);
        return 1;
    }

    public void deleteMovieFromWatchList(FilmList filmList, Movie movie) {
        filmList.getMovies().remove(movie.getTitle());
        //deleting the film list from that movies film lists
        movie.getFilmLists().remove(filmList);
    }


    public void addDeleteMovieFromFilmList(FilmList filmList) {
        String choice;
        Scanner scanner = new Scanner(System.in);
        filmList.showMovies();
        while (true) {
            Movie movie;
            System.out.println("1. Exit film list editing menu");
            System.out.println("2. Add movie to film list");
            System.out.println("3. Delete movie from film list");
            choice = scanner.nextLine();
            if (choice.equals("1"))
                break;
            System.out.println("Enter the name of the movie you want to add to your watch list");
            movie = DataBase.getMovies().get(scanner.nextLine());
            if (movie == null) {
                System.out.println("This movie does not exist");
                continue;
            }
            System.out.println("Enter the name of the watch list you want to edit");
            filmList = this.getFilmLists().get(scanner.nextLine());
            if (filmList == null) {
                System.out.println("You do not have a watch list with this name");
                continue;
            }
            switch (choice) {
                case "2":
                    this.addMovieToWatchList(filmList, movie);
                    break;
                case "3":
                    this.deleteMovieFromWatchList(filmList, movie);
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    public void filmListFunctionality() {
        Scanner scanner = new Scanner(System.in);
//        int output = this.showWatchLists();
        String watchListName;
        String choice = "m";
        Movie movie;
        FilmList filmList;
        while (!choice.equals("0")) {
            System.out.println("0. Exit film list menu");
            System.out.println("1. Open my watch lists");
            System.out.println("2. Open favorites");
            System.out.println("3. Open classics to see");
            System.out.println("4. Make new watch list");
            choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    break;
                case "1":
                    int output = this.showWatchLists();
                    if (output == 1) {
                        System.out.println("This list is empty");
                        break;
                    }
                    System.out.println("Enter the name of the watch list you want to see/edit");
                    choice = scanner.nextLine();
                    filmList = this.getFilmLists().get(choice);
                    if (filmList == null) {
                        System.out.println("The entered string does not match any watch list name");
                        break;
                    }
                    this.addDeleteMovieFromFilmList(filmList);
                    break;
                case "2":
                    FilmList favorites = this.filmLists.get("favorites");
                    this.addDeleteMovieFromFilmList(favorites);
                    break;
                case "3":
                    FilmList classicsToSee = this.filmLists.get("classicsToSee");
                    classicsToSee.showMovies();
                    break;
                case "4":
                    System.out.println("Enter new watch list name");
                    watchListName = scanner.nextLine();
                    output = this.createWatchList(watchListName);
                    if (output == 0)
                        System.out.println("A watch list with this name already exists among your watch lists");
                    else System.out.println("Watch list created successfully");
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }

    }

    //_____________________________________________________       admin      ____________________________________________________________
    public void changeUserConfiguration(UserType userType) {
        String name;
        String lastName;
        int age;
        Gender gender;
        User user;
        String nationalID;
        int option = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter the username of the user you want to make admin");
        System.out.println("Username:");
        String userName = scanner.nextLine();
        user = DataBase.getUserMap().get(userName);
        if (user == null) {
            System.out.println("This user does not exist");
            return;
        }
        System.out.println("Please complete user Identification information");
        System.out.println("Name:");
        name = scanner.nextLine();

        System.out.println("Lastname:");
        lastName = scanner.nextLine();

        System.out.println("Age:");
        String test;
        while (!isInteger(test = scanner.nextLine()))
            System.out.println("Invalid input, please enter age in digits");
        age = Integer.parseInt(test);

        System.out.println("Choose the gender: 1.Female 2.Male 3.Other");
        while (true) {
            if (isInteger(test = scanner.nextLine()))
                option = Integer.parseInt(test);
            if (option == 1 || option == 2 || option == 3) break;
            System.out.println("The number you entered is not a valid option");
        }
        if (option == 1) gender = Gender.FEMALE;
        else if (option == 2) gender = Gender.MALE;
        else gender = Gender.OTHER;

        System.out.println("Enter nationalID");
        nationalID = scanner.nextLine();
        //changing the account
        user.makeAdminOrEditor(userType, this, name, lastName, age, nationalID, gender);
        System.out.println("User configuration completed successfully");

    }

    public void adminMovieSetting() {

        String choice = "";
        Scanner scanner = new Scanner(System.in);
        Movie movie;

        while (!choice.equals("0")) {
            System.out.println("0. Exit movie edit menu");
            System.out.println("1. Add a movie to data base");
            System.out.println("2. Delete a movie from data base");
            System.out.println("3. Edit movie detail");
            choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    break;
                case "1":
                    String option = "";
                    String title, picturePath, trailerPath, movieDescription;
                    LocalDate releaseDate = null;
                    int budget = 0;
                    Language language = null;
                    ArrayList<Genre> genres = new ArrayList<>();
                    String test;
                    System.out.println("Title:");
                    title = scanner.nextLine();
                    System.out.println("Picture path:");
                    picturePath = scanner.nextLine();
                    System.out.println("Trailer path:");
                    trailerPath = scanner.nextLine();
                    System.out.println("Movie description:");
                    movieDescription = scanner.nextLine();
                    // Prompt the user to enter a date
                    while (releaseDate == null) {
                        try {
                            System.out.print("Enter a date (yyyy-MM-dd): ");
                            // Parse the user input into a LocalDate object
                            releaseDate = LocalDate.parse(scanner.nextLine());
                        } catch (Exception e) {
                            // Handle the case where the user enters an invalid date format
                            System.out.println("Invalid date format. Please enter a date in the format yyyy-MM-dd.");
                        }
                    }
                    while (budget <= 0) {
                        System.out.println("Movie budget:");
                        if (!isInteger(test = scanner.nextLine()))
                            System.out.println("invalid input");
                        else budget = Integer.parseInt(test);
                        if (budget <= 0)
                            System.out.println("The budget must be a positive amount");
                    }
                    language = this.chooseLanguage();
                    if (language == null) {
                        System.out.println("You didn't choose a language");
                        return;
                    }
                    genres = this.chooseGenre();
                    if (genres == null) {
                        System.out.println("You didn't properly choose genres");
                        break;
                    }
                    //now all the data a movie needs is provided
                    Movie.addMovie(this, title, picturePath, trailerPath, movieDescription, releaseDate, budget, genres, language);
                    System.out.println("Warning: The movie you created does not have any acting records go to acting records setting to add records to it");
                    break;
                case "2":
                    System.out.println("Please enter the name of the movie you want to delete");
                    if ((movie = DataBase.getMovies().get(scanner.nextLine())) == null) {
                        System.out.println("This movie does not exist in the data base");
                        break;
                    }
                    movie.deleteMovie(this);
                    System.out.println("Movie deleted successfully");
                    break;
                case "3":
                    System.out.println("To edit movie detail you should first publish an edit and then confirm it manually");
                    System.out.println("This is to keep track of all the edits made on the detail");
                    this.publishEdit();
                    this.adminEditSetting();
                    break;
                //check if admin has a method to delete reviews of a movie already if not add it here
//                case "4":
//                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }


    public void adminReviewSetting() {
        Movie movie = null;
        Scanner scanner = new Scanner(System.in);
        String option;
        String inputCheck = "";
        System.out.println("Please enter the name of the movie that you want to see it's reviews");
        while ((!inputCheck.equals("0")) && (movie == null)) {
            System.out.println("Enter movie name");
            movie = DataBase.getMovies().get(scanner.nextLine());
            System.out.println("This movie does not exist in the data base. Enter 0 to exit review setting and any other character to proceed with choosing a movie again");
            inputCheck = scanner.nextLine();
        }
        if (inputCheck.equals("0"))
            return;
        System.out.println("0. Exit review menu");
        System.out.println("1. Add review or reply to a review with admin account");
        System.out.println("2. Delete a review");
        System.out.println("3. Show replies and delete replies of a review");
        while (!(option = scanner.nextLine()).equals("0")) {
            String test;
            int choice;
            Review review;
            Review reply;
            String content;
            Boolean isSpoiler = false;
            int movieReviewNumber;
            switch (option) {
                case "1":
                    this.reviewFunctionality(movie);
                    break;
                case "2":
                    movieReviewNumber = movie.showReviews();
                    System.out.println("Choose the number of the review that you wish to remove or enter 0 to exit");
                    while (!(test = scanner.nextLine()).equals("0")) {
                        if (isInteger(test)) {
                            choice = Integer.parseInt(test);
                            if (choice > movieReviewNumber) {
                                System.out.println("There is not a review with this number. Enter your choice again or enter 0 to exit");
                                continue;
                            }
                            review = movie.getReviews().get(choice);
                            review.deleteReview(this);
                        } else System.out.println("The character you entered is not an option");
                    }
                    break;
                case "3":
                    movieReviewNumber = movie.showReviews();
                    ArrayList<Review> reviewReplies;
                    System.out.println("Choose the number of the review that you wish to see the replies or enter 0 to exit");
                    while (!(test = scanner.nextLine()).equals("0")) {
                        if (isInteger(test)) {
                            choice = Integer.parseInt(test);
                            if (choice > movieReviewNumber) {
                                System.out.println("There is not a review with this number. Enter your choice again or enter 0 to exit");
                                continue;
                            }
                            review = movie.getReviews().get(choice);
                            reviewReplies = review.showReplies();
                            System.out.println("Choose the number of the reply that you wish to remove or enter 1 to exit");
                            int replyNumber = reviewReplies.size();
                            while (!(test = scanner.nextLine()).equals("1")) {
                                if (isInteger(test)) {
                                    choice = Integer.parseInt(test);
                                    if (choice > replyNumber) {
                                        System.out.println("There is not a reply with this number. Enter your choice again or enter 0 to exit");
                                        continue;
                                    }
                                    //it might need a little more work
                                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    reply = reviewReplies.get(choice);
                                    movie.getReviews().remove(reply);
                                } else System.out.println("The character you entered is not an option");
                            }

                        } else System.out.println("The character you entered is not an option");
                    }
                    break;

            }
        }

    }

    public void adminPeopleSetting() {
        String choice = "";
        Scanner scanner = new Scanner(System.in);
        while (!choice.equals("0")) {
            System.out.println("0. Exit people setting");
            System.out.println("1. Add Person to data base");
            System.out.println("2. Delete Person from data base");
            System.out.println("3. Edit person biography");
            choice = scanner.nextLine();
            //the third option should get complete when the editor part is complete
            switch (choice) {
                case "0":
                    break;
                case "1":
                    String name;
                    String lastName;
                    int age;
                    Gender gender;
                    String nationalID;
                    int option = 0;
                    while (true) {
                        System.out.println("Enter 1 to exit adding person menu and any other character to proceed");
                        choice = scanner.nextLine();
                        if (choice.equals("1")) break;
                        System.out.println("Name:");
                        name = scanner.nextLine();

                        System.out.println("Lastname:");
                        lastName = scanner.nextLine();

                        System.out.println("Age:");
                        String test;
                        while (!isInteger(test = scanner.nextLine()))
                            System.out.println("Invalid input, please enter age in digits");
                        age = Integer.parseInt(test);

                        System.out.println("Choose the gender: 1.Female 2.Male 3.Other");
                        while (true) {
                            if (isInteger(test = scanner.nextLine()))
                                option = Integer.parseInt(test);
                            if (option == 1 || option == 2 || option == 3) break;
                            System.out.println("The number you entered is not a valid option");
                        }
                        if (option == 1) gender = Gender.FEMALE;
                        else if (option == 2) gender = Gender.MALE;
                        else gender = Gender.OTHER;

                        System.out.println("Enter nationalID");
                        nationalID = scanner.nextLine();

                        createPerson(this, name, lastName, age, nationalID, gender, new HashMap<>());
                        System.out.println("Person successfully created and added to data base");
                        System.out.println("Warning: this person does not have any acting records please go to acting records setting if you wish to add acting records");
                    }
                    break;
                case "2":
                    String personNationalId;
                    Person person;
                    int output;
                    System.out.println("Enter the nationalID of the person you wish to delete from data base");
                    personNationalId = scanner.nextLine();
                    person = DataBase.getPeopleMap().get(personNationalId);
                    if (person == null) {
                        System.out.println("A Person with this nationalID does not exist");
                        break;
                    }
                    person.deletePerson();
                    System.out.println("Person deleted from data base successfully");
                    System.out.println("All the acting records of this person deleted from according movies successfully");
                    System.out.println("Person deleted from following lists of all followers successfully");
                    break;
                case "3":
                    System.out.println("To edit biography detail you should first publish an edit and then confirm it manually");
                    System.out.println("This is to keep track of all the edits made on the detail");
                    this.publishEdit();
                    this.adminEditSetting();
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    public void adminUsersSetting() {
        Scanner scanner = new Scanner(System.in);
        String choice = "3";
        while (!choice.equals("0")) {
            String username;
            String password;
            User user;
            System.out.println("0. Exit Users setting menu");
            System.out.println("1. Add User to data base");
            System.out.println("2. Delete User from data base");
            System.out.println("3. Change Users password to ban them from the site");
            System.out.println("4. Change a Users username");
            choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    break;
                case "1":
                    System.out.println("Enter 1 to exit adding User menu and any other character to add a user to data base");
                    choice = scanner.nextLine();
                    if (choice.equals("1"))
                        break;
                    System.out.println("Enter user name");
                    username = scanner.nextLine();
                    System.out.println("Enter password");
                    password = scanner.nextLine();
                    User.signUp(username, password);
                    System.out.println("User successfully created and added to database");
                    break;
                case "2":
                    System.out.println("Enter 1 to exit deleting User menu and any other character to add a user to data base");
                    choice = scanner.nextLine();
                    if (choice.equals("1"))
                        break;
                    System.out.println("Enter user name");
                    username = scanner.nextLine();
                    user = searchUserInDB(username);
                    if (user == null) {
                        System.out.println("A user with this username does not exist");
                        break;
                    }
                    user.deleteUserFromDB();
                    System.out.println("User successfully deleted from database");
                    break;
                case "3":
                    System.out.println("Enter 1 to exit banning User menu and any other character to add a user to data base");
                    choice = scanner.nextLine();
                    if (choice.equals("1"))
                        break;
                    System.out.println("Enter user name");
                    username = scanner.nextLine();
                    user = searchUserInDB(username);
                    if (user == null) {
                        System.out.println("A user with this username does not exist");
                        break;
                    }
                    System.out.println("Enter the new password for this user account");
                    password = scanner.nextLine();
                    user.setPassword(password);
                    System.out.println("User successfully banned from site");
                    break;
                case "4":
                    System.out.println("Enter 1 to exit changing Username menu and any other character to add a user to data base");
                    choice = scanner.nextLine();
                    if (choice.equals("1"))
                        break;
                    System.out.println("Enter previous user name");
                    username = scanner.nextLine();
                    user = searchUserInDB(username);
                    if (user == null) {
                        System.out.println("A user with this username does not exist");
                        break;
                    }
                    System.out.println("Enter the new username for this user account");
                    username = scanner.nextLine();
                    user.setUserName(username);
                    System.out.println("Username successfully changed");
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    //-____________________________________________________________________________________________________________________________
    public static Occupation chooseOccupation() {
        Scanner scanner = new Scanner(System.in);
        Occupation occupation = null;
        HashMap<Integer, Occupation> occupationHashMap;
        String test;
        while (occupation == null) {
            System.out.println("1. Choose occupation from the list");
            System.out.println("2. Create a new occupation");
            test = scanner.nextLine();
            if (test.equals("1")) {
                occupationHashMap = Occupation.showOccupations();
                if (occupationHashMap == null)
                    continue;
                System.out.println("Enter the number of the occupation you want to choose");
                test = scanner.nextLine();
                if (!isInteger(test)) {
                    System.out.println("The character you entered is not a valid option");
                    break;
                }
                occupation = occupationHashMap.get(Integer.parseInt(test));
                if (occupation == null)
                    System.out.println("The number you entered does not match any occupation on the list");
            } else if (test.equals("2")) {
                System.out.println("Enter the occupation you want to create");
                test = scanner.nextLine();
                occupation = Occupation.addOccupation(test);
                System.out.println("The occupation added to the data base");
            } else System.out.println("The character you entered is not a valid option");
        }
        return occupation;
    }

    public ArrayList<Genre> chooseGenre() {
        String option = "";
        String test = "";
        Scanner scanner = new Scanner(System.in);
        ArrayList<Genre> genres = null;
        while (!option.equals("0")) {
            Genre genre = null;
            while (genre == null) {
                System.out.println("Any genre that you choose will be added to the list until you exit the menu");
                System.out.println("0. Exit genre menu");
                System.out.println("1. add a genre to the list");

                if (this.getUserType() != UserType.NORMALACCOUNT)
                    System.out.println("2. Enter a new genre and add it to movies genres");

                option = scanner.nextLine();
                if (option.equals("0"))
                    break;
                else if (option.equals("1")) {
                    System.out.println("Enter the genre you want to choose");
                    test = scanner.nextLine();
                    genre = DataBase.getGenreHashMap().get(test);
                    if (genre == null) System.out.println("This genre is not in the database");
                } else if ((option.equals("2")) && (this.getUserType() != UserType.NORMALACCOUNT)) {
                    System.out.println("Enter the genre you want to create");
                    test = scanner.nextLine();
                    genre = Genre.addGenre(test);
                    DataBase.getGenreHashMap().put(test, genre);
                    System.out.println("Genre successfully created and added to the database");
                } else System.out.println("The character you entered is not a valid option");
                if (genre != null)
                    genres.add(genre);
            }
        }
        return genres;
    }

    public Language chooseLanguage() {
        Language language = null;
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer, Language> languageHashMap;
        String test = "";
        while (!test.equals("0")) {
            System.out.println("0. Exit language menu");
            System.out.println("1. Choose a language");
            if (this.getUserType() != UserType.NORMALACCOUNT)
                System.out.println("2. Enter a new language ( a new language will be created and added to the database)");
            test = scanner.nextLine();
            if (test.equals("0"))
                break;
            if (test.equals("1")) {
                languageHashMap = Language.showLanguages();
                if (languageHashMap == null) {
                    System.out.println("This list is empty");
                    continue;
                }
                System.out.println("Enter the number of the language you want to choose");
                String option;
                if (!isInteger(option = scanner.nextLine())) {
                    System.out.println("The character you entered is not a number");
                    continue;
                }
                language = languageHashMap.get(Integer.parseInt(option));
                break;
            } else if ((test.equals("2")) && (this.getUserType() != UserType.NORMALACCOUNT)) {
                System.out.println("Enter the language you want to create");
                test = scanner.nextLine();
                language = Language.addLanguage(test);
                System.out.println("The language added to the data base");
                break;
            } else System.out.println("The character you entered is not a valid option");

        }
        return language;
    }

    //-____________________________________________________________________________________________________________________________
    public void adminActingRecordSetting() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equals("0")) {
            System.out.println("0. Exit acting records menu");
            System.out.println("1. Add acting record (The record will be added to both the persons records and the movie)");
            System.out.println("2. Delete acting record (The record will be deleted from both the persons records and the movie)");
            //this option should be completed after edit menu is complete
            //1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            System.out.println("3. Edit acting record information");
            choice = scanner.nextLine();
            String nationalID;
            Person person;
            Movie movie;
            Occupation occupation;
            String test;
            ActingRecord actingRecord;
            double payment;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    while (true) {
                        System.out.println("Enter 1 to exit the add acting record menu and any other character to proceed with the process");
                        choice = scanner.nextLine();
                        if (choice.equals("1"))
                            break;
                        System.out.println("Enter persons nationalID");
                        nationalID = scanner.nextLine();
                        person = DataBase.getPeopleMap().get(nationalID);
                        if (person == null) {
                            System.out.println("Person with this nationalID does not exist");
                            break;
                        }
                        System.out.println("Enter the name of the movie this acting record is for");
                        movie = Movie.searchMovieInDB(scanner.nextLine());
                        if (movie == null) {
                            System.out.println("Movie with this name does not exit");
                            break;
                        }
                        occupation = chooseOccupation();
                        if (occupation == null) {
                            System.out.println("Occupation was not entered correctly.");
                            break;
                        }
                        System.out.println("Enter the payment of this person in this movie");
                        System.out.println("Warning: Make sure you enter the payment in floating point number format");
                        if (!isDouble(test = scanner.nextLine())) {
                            System.out.println("The character you entered is not a valid option");
                            break;
                        }
                        payment = Double.parseDouble(test);
                        ActingRecord.addActingRecord(this, person, movie, occupation, payment);
                        System.out.println("Acting record successfully added both to movie and the person");
                    }
                    break;
                case "2":
                    HashMap<Integer, ActingRecord> actingRecordHashMap;
                    System.out.println("Enter 1 to exit delete acting record menu or any other character to proceed");
                    choice = scanner.nextLine();
                    if (choice.equals("1"))
                        break;
                    System.out.println("Enter the name of the nationalID of the person you want to choose a record from");
                    nationalID = scanner.nextLine();
                    person = DataBase.getPeopleMap().get(nationalID);
                    if (person == null) {
                        System.out.println("Person with this nationalID does not exist");
                        break;
                    }
                    actingRecordHashMap = person.showActingRecords();
                    System.out.println("Enter the number of the acting record you want to choose");
                    test = scanner.nextLine();
                    if (!isInteger(test)) {
                        System.out.println("The character you entered is not a valid option");
                        break;
                    }
                    actingRecord = actingRecordHashMap.get(Integer.parseInt(test));
                    if (actingRecord == null) {
                        System.out.println("The number you entered does not match any occupation on the list");
                        break;
                    }
                    actingRecord.deleteActingRecord(this);
                    System.out.println("Acting record deleted successfully");
                    break;
//                case "3":
//                    System.out.println("Publish an edit the edit will automatically be confirmed and applied");
//                    this.publishEdit();
//
//                    break;
                default:
                    System.out.println("The character you entered is not a valid option");

            }


        }
    }

    //_____________________________________________________       user     ____________________________________________________________
    public void showAllUserData() {
        System.out.printf("username: %s\n", this.getUserName());
        System.out.printf("user type: %s\n", this.getUserType());
        System.out.println("Following list:");
        this.showFollowings();
        System.out.println("Watch lists:");
        this.showFollowings();
        System.out.println("Rated movies:");
        this.showRatedMovies();

    }

    //_____________________________________________________       post and group     ____________________________________________________________
    public HashMap<Integer, Group> showGroups() {
        HashMap<Integer, Group> groupHashMap = new HashMap<>();
        Collection<Group> groups = this.groups.values();
        int i = 1;
        for (Group group : groups) {
            System.out.printf("%d. %s\n", i, group.getName());
            groupHashMap.put(i, group);
            i++;
        }
        return groupHashMap;
    }

    public void groupFunctionality() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        HashMap<Integer, Group> groupHashMap = this.showGroups();
        System.out.println("Choose a group");
        choice = scanner.nextLine();
        if (!isInteger(choice)) {
            System.out.println("The string you entered is not a number");
            return;
        }
        Group group = groupHashMap.get(Integer.parseInt(choice));
        if (group == null) {
            System.out.println("The number you entered does not match any group");
            return;
        }
        while (!choice.equals("0")) {
            System.out.println("0. Exit Group menu");
            System.out.println("1. See Group posts and delete posts");
            System.out.println("2. See Group participants");
            System.out.println("3. Add participant");
            System.out.println("4. delete participant");
            choice = scanner.nextLine();
            int i;
            User participant;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    i = 1;
                    Set<Post> groupPosts = group.getPosts().keySet();
                    HashMap<Integer, Post> groupPostsHashMap = new HashMap<>();
                    for (Post post : groupPosts) {
                        System.out.printf("%d\n", i);
                        post.showPost();
                        groupPostsHashMap.put(i, post);
                        i++;
                    }
                    System.out.println("Enter the number of the post you want to delete or zero to exit list");
                    choice = scanner.nextLine();
                    if (choice.equals("0"))
                        break;
                    if (!isInteger(choice)) {
                        System.out.println("The string you entered is not a number");
                        return;
                    }
                    Post postToRemove = groupPostsHashMap.get(Integer.parseInt(choice));
                    if (postToRemove == null) {
                        System.out.println("The number you entered does not match any post");
                    }
                    group.getPosts().remove(postToRemove);
                    break;
                case "2":
                    i = 1;
                    ArrayList<User> participants = group.getParticipants();
                    for (User user : participants) {
                        System.out.printf("%d. %s\n", i, user.getUserName());
                    }
                    break;
                case "3":
                    System.out.println("Enter username of the user you want to add to group");
                    participant = DataBase.getUserMap().get(scanner.nextLine());
                    if (participant == null) {
                        System.out.println("User with this username does not exist");
                        break;
                    }
                    group.participants.add(participant);
                    System.out.println("Participant added successfully");
                    break;
                case "4":
                    System.out.println("Enter username of the user you want to delete from group");
                    participant = DataBase.getUserMap().get(scanner.nextLine());
                    Boolean output = group.getParticipants().remove(participant);
                    if (output)
                        System.out.println("Participant deleted successfully");
                    else
                        System.out.println("User with this username is not in this group");
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    public void directFunctionality() {
        String choice = "";
        Scanner scanner = new Scanner(System.in);
        while (!choice.equals("0")) {
            System.out.println("0. Exit direct menu");
            System.out.println("1. Show direct");
            choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    break;
                case "1":
                    Set<User> directUsers = this.getDirect().keySet();
                    for (User user : directUsers) {
                        System.out.println(user.getUserName());
                    }
                    System.out.println("Enter the username to open pv");
                    User chosenUser = DataBase.getUserMap().get(scanner.nextLine());
                    ArrayList<Post> chosenUserPosts = this.getDirect().get(chosenUser);
                    if (chosenUserPosts == null) {
                        System.out.println("This user is not in your direct");
                        break;
                    }
                    for (Post post : chosenUserPosts) {
                        post.showPost();
                    }
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }
        }
    }

    public void postFunctionality() {
        String choice = "";
        Scanner scanner = new Scanner(System.in);
        while (!choice.equals("0")) {
            System.out.println("0. Exit post menu");
            System.out.println("1. Add post");
            System.out.println("2. Show my posts");
            System.out.println("3. Show a users post and reply to that post or forward post to a user or group");
            System.out.println("4. Show my replies");
            choice = scanner.nextLine();
            String content;
            String picturePath;
            String videoPath;
            User user;
            String username;
            Post otherUsersPost;
            ArrayList<Post> posts;
            switch (choice) {
                case "0":
                    break;
                case "1":
                    System.out.println("Enter the content of your post");
                    content = scanner.nextLine();
                    System.out.println("Enter picture path");
                    picturePath = scanner.nextLine();
                    System.out.println("Enter video path");
                    videoPath = scanner.nextLine();
                    Post.addPost(content, picturePath, videoPath, this, null);
                    System.out.println("Posted");
                    break;
                case "2":
                    posts = this.getPosts();
                    for (Post post : posts) {
                        post.showPost();
                    }
                    break;
                case "3":
                    System.out.println("Enter the name of the user");
                    username = scanner.nextLine();
                    user = DataBase.getUserMap().get(username);
                    if (user == null) {
                        System.out.println("User not found");
                        break;
                    }
                    posts = user.getPosts();
                    if (posts.isEmpty()) {
                        System.out.println("This list is empty");
                        break;
                    }
                    for (Post post : posts) {
                        post.showPost();
                    }
                    System.out.println("Enter 0 to exit or the number of the post you want to reply or forward");
                    choice = scanner.nextLine();
                    if (choice.equals("0"))
                        break;
                    if (!isInteger(choice)) {
                        System.out.println("The string you entered is not a number");
                        break;
                    }
                    int option = Integer.parseInt(choice);
                    if ((option > (posts.size())) || (option < 0)) {
                        System.out.println("The number you entered does not match any post");
                        break;
                    }
                    otherUsersPost = posts.get(option);
                    System.out.println("0. Exit this menu");
                    System.out.println("1. Reply to this post");
                    System.out.println("2. Forward this post to a user");
                    System.out.println("3. Forward this post to a group");
                    choice = scanner.nextLine();
                    switch (choice) {
                        case "0":
                            break;
                        case "1":
                            System.out.println("Enter the content of your post");
                            content = scanner.nextLine();
                            System.out.println("Enter picture path");
                            picturePath = scanner.nextLine();
                            System.out.println("Enter video path");
                            videoPath = scanner.nextLine();
                            Post.addPost(content, picturePath, videoPath, this, otherUsersPost);
                            System.out.println("Reply successfully submitted");
                            break;
                        case "2":
                            System.out.println("Enter the username of the user you want to forward this post to");
                            user = DataBase.getUserMap().get(scanner.nextLine());
                            if (user == null) {
                                System.out.println("This user does not exist");
                                break;
                            }
                            ArrayList<Post> directPosts = user.getDirect().get(this);
                            if (directPosts == null) {
                                user.getDirect().put(this, new ArrayList<>());
                                this.getDirect().put(user, new ArrayList<>());
                            }
                            user.getDirect().get(this).add(otherUsersPost);
                            this.getDirect().get(user).add(otherUsersPost);
                            System.out.println("post successfully forwarded to this user");
                            break;
                        case "3":
                            HashMap<Integer, Group> groupHashMap = this.showGroups();
                            if (groupHashMap.isEmpty()) {
                                System.out.println("This list is empty");
                                break;
                            }
                            System.out.println("Enter the number of the group you want to forward this post to");
                            choice = scanner.nextLine();
                            if (!isInteger(choice)) {
                                System.out.println("The string you entered is not a number");
                                break;
                            }
                            Group group = groupHashMap.get(Integer.parseInt(choice));
                            if (group == null) {
                                System.out.println("The number you entered does not match any group");
                                break;
                            }
                            group.getPosts().put(otherUsersPost, this);
                            System.out.println("Post sent to group successfully");
                            break;

                    }
                    break;
                case "4":
                    posts = this.getPosts();
                    for (Post post : posts) {
                        if (post.getRepliedTo() != null) {
                            post.showPost();
                        }
                    }
                    break;
                default:
                    System.out.println("The character you entered is not a valid option");
            }

        }

    }

    public void makeEditor(User admin) {
        if (admin.getUserType() == UserType.ADMIN)
            this.setUserType(UserType.EDITOR);
    }

    public void makeEditorFunctionality() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the username of the user you want to make editor");
        User user = DataBase.getUserMap().get(scanner.nextLine());
        if (user != null) {
            user.makeEditor(this);
        } else {
            System.out.println("User with this username does not exist in database");
        }
    }

    public void normalAccountFunctionality() {

    }

    //_____________________________________________________       .........     ____________________________________________________________
    public static void loginPage() {
        Scanner scanner = new Scanner(System.in);
        int choice = 3;
        int loginPageChoice;
        int option;
        int output;
        String test;
        String passwordCheck;
        String newData;
        User user;
        while (choice != 0) {
            user = null;
            option = 1;
            loginPageChoice = 10;
            String username;
            String password;
            System.out.println("Welcome to IMDb application please choose 1 for sign in and 2 if you don't already have an account or 0 to exit the program.");
            if (isInteger(test = scanner.nextLine())) {
                choice = Integer.parseInt(test);
            } else choice = 3;
            //exit
            if (choice == 0) {
                System.out.println("Exiting the program...");
                break;
            } else if (choice == 1) {
                while (user == null) {
                    System.out.println("Enter username and password");
                    username = scanner.nextLine();
                    password = scanner.nextLine();
                    if ((user = User.signIn(username, password)) == null) {
                        System.out.println("Incorrect username or password");
                        System.out.println("Enter 0 to exit sign in and any other number if you want to try signing in again");
                        test = scanner.nextLine();
                        if (isInteger(test))
                            option = Integer.parseInt(test);
                        if (option == 0) break;
                    }
                }
            }

            //sign up
            else if (choice == 2) {
                while (user == null) {
                    System.out.println("Enter username and password");
                    username = scanner.nextLine();
                    password = scanner.nextLine();
                    user = User.signUp(username, password);
                    if (user == null) {
                        System.out.println("The username you entered already exists Enter 0 to exit sign up and any other character to choose another username.");
                        test = scanner.nextLine();
                        if (isInteger(test))
                            option = Integer.parseInt(test);
                        if (option == 0) break;
                    }
                }
            } else {
                System.out.println("The character you entered is not an option.");
                continue;
            }
            if (((choice == 1) || (choice == 2)) && (option != 0)) {
                //show all the data that the user should see
                //user homepage
                while (loginPageChoice != 0) {
                    Movie movie = null;
                    choice = 1;
                    loginPageChoice = 10;
                    String choice2;
                    int i = 1;
                    ArrayList<Movie> recommendation;
                    Boolean flag = false;

                    if (user.getUserType() == UserType.NORMALACCOUNT ) {


                        System.out.println("0. Exit this account");
                        System.out.println("1. See 20 newest movies");
                        System.out.println("2. See IMDb's top 250 movies");
                        System.out.println("3. Open following list and follow a Person or a User");
                        System.out.println("4. See movies you rated and change or delete rates");
                        System.out.println("5. Open Setting");
                        System.out.println("6. Open film list menu");
                        //this one is not created yet as well
                        System.out.println("7. Search for movies");
                        System.out.println("8. Search for user");
                        System.out.println("9. Search for movies based on genre");
                        System.out.println("10. Search for movies based on language");
                        System.out.println("11. Search for movies based on release year");
                        System.out.println("12. See recommended movies based on genre");
                        System.out.println("13. See recommended movies based on language");
                        System.out.println("14. See recommended movies based on favorite actors and directors");
                        System.out.println("15. See popular movies");
                        System.out.println("16. Post menu");
                        System.out.println("17. Direct");
                        System.out.println("18. Groups");
                        if (isInteger((test = scanner.nextLine())))
                            loginPageChoice = Integer.parseInt(test);
                        else {
                            System.out.println("The character you entered is not a valid option");
                            break;
                        }
                        HashMap<Integer, Movie> chooseMoviehashmap;
                        String movieChoice;
                        switch (loginPageChoice) {
                            case 0:
                                break;
                            case 1:
                                while (true) {
                                    chooseMoviehashmap = Movie.showNewestMovies();
                                    System.out.println("Enter 0 to exit this page");
                                    System.out.println("Choose a movie to see information/add review/...");
                                    movieChoice = scanner.nextLine();
                                    if (movieChoice.equals("0"))
                                        break;
                                    if (!isInteger(movieChoice)) {
                                        System.out.println("The character you entered is not a valid option");
                                        break;
                                    }
                                    movie = chooseMoviehashmap.get(Integer.parseInt(movieChoice));
                                    if (movie == null) {
                                        System.out.println("The number you entered does not match any movie");
                                        break;
                                    }
                                    user.movieFunctionality(movie);
                                }
                                break;
                            case 2:
                                while (true) {
                                    chooseMoviehashmap = Movie.show250TopMovies();
                                    System.out.println("Enter 0 to exit this page");
                                    System.out.println("Choose a movie to see information/add review/...");
                                    movieChoice = scanner.nextLine();
                                    if (movieChoice.equals("0"))
                                        break;
                                    if (!isInteger(movieChoice)) {
                                        System.out.println("The character you entered is not a valid option");
                                        break;
                                    }
                                    movie = chooseMoviehashmap.get(Integer.parseInt(movieChoice));
                                    if (movie == null) {
                                        System.out.println("The number you entered does not match any movie");
                                        break;
                                    }
                                    user.movieFunctionality(movie);
                                }
                                break;
                            case 3:
                                user.followingFunctionality();
                                break;
                            case 4:
                                while (choice != 0) {
                                    chooseMoviehashmap = user.showRatedMovies();
                                    System.out.println("Enter 0 to exit this page");
                                    System.out.println("Choose a movie to see information/add review/...");
                                    movieChoice = scanner.nextLine();
                                    if (movieChoice.equals("0"))
                                        break;
                                    if (!isInteger(movieChoice)) {
                                        System.out.println("The character you entered is not a valid option");
                                        break;
                                    }
                                    movie = chooseMoviehashmap.get(Integer.parseInt(movieChoice));
                                    if (movie == null) {
                                        System.out.println("The number you entered does not match any movie");
                                        break;
                                    }
                                    user.movieFunctionality(movie);
                                }
                                break;
                            case 5:
                                while (loginPageChoice != 3) {
                                    loginPageChoice = 10;
                                    System.out.println("1. Change password");
                                    System.out.println("2. Change username");
                                    System.out.println("3. Exit setting");
                                    if (isInteger(test = scanner.nextLine()))
                                        loginPageChoice = Integer.parseInt(test);
                                    if (loginPageChoice == 3)
                                        break;
                                    if (loginPageChoice == 1) {
                                        System.out.println("Please enter your current password");
                                        passwordCheck = scanner.nextLine();
                                        System.out.println("Please enter your new password");
                                        newData = scanner.nextLine();
                                        output = user.changePassword(passwordCheck, newData);
                                    } else if (loginPageChoice == 2) {
                                        System.out.println("Please enter your current password");
                                        passwordCheck = scanner.nextLine();
                                        System.out.println("Please enter your new username");
                                        newData = scanner.nextLine();
                                        output = user.changeUsername(passwordCheck, newData);
                                    } else {
                                        System.out.println("The number you entered is not a valid option ");
                                        continue;
                                    }
                                    if (output == 0)
                                        System.out.println("Couldn't apply change because your password was incorrect");
                                    else {
                                        System.out.println("Change applied successfully");
                                    }
                                }
                                break;
                            //watch lists functionality
                            case 6:
                                user.filmListFunctionality();
                                break;
                            //search for movie
                            case 7:
                                System.out.println("Enter the name of the movie");
                                movie = DataBase.getMovies().get(scanner.nextLine());
                                if (movie == null) {
                                    System.out.println("Movie with this name does not exist");
                                    break;
                                }
                                user.movieFunctionality(movie);
                                break;
                            case 8:
                                System.out.println("Enter username");
                                User user2 = DataBase.getUserMap().get(scanner.nextLine());
                                if (user2 == null) {
                                    System.out.println("user with this username does not exist");
                                    break;
                                }
                                user2.showAllUserData();
                                break;
                            case 9:
                                ArrayList<Genre> genres = user.chooseGenre();
                                ArrayList<Movie> moviesByGenre = null;
                                if (genres != null) {
                                    moviesByGenre = Movie.findMoviesWithSpecificGenres(genres);
                                }
                                if (moviesByGenre == null) {
                                    System.out.println("There are no movies with the genres you chose");
                                    break;
                                }
                                for (Movie movie2 : moviesByGenre) {
                                    System.out.printf("%d. %s\n", i, movie2.getTitle());
                                    i++;
                                }
                                System.out.println("choose a movie to see more about");
                                //add movie functionality
                                if (isInteger(choice2 = scanner.nextLine()))
                                    movie = moviesByGenre.get(Integer.parseInt(choice2));
                                if (movie != null)
                                    user.movieFunctionality(movie);
                                break;
                            case 10:
                                ArrayList<Movie> moviesByLanguage = null;
                                Language language = user.chooseLanguage();
                                if (language == null) {
                                    System.out.println("You did not properly choose a language");
                                    break;
                                }
                                moviesByLanguage = Movie.findMoviesWithSpecificLanguage(language);
                                if (moviesByLanguage.isEmpty()) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie2 : moviesByLanguage) {
                                    System.out.printf("%d. %s\n", i, movie2.getTitle());
                                    i++;
                                }
                                System.out.println("choose a movie to see more about");
                                //add movie functionality
                                if (isInteger(choice2 = scanner.nextLine()))
                                    movie = moviesByLanguage.get(Integer.parseInt(choice2) - 1);
                                if (movie != null)
                                    user.movieFunctionality(movie);
                                break;
                            case 11:
                                System.out.println("Enter year in format yyyy ");
                                test = scanner.nextLine();
                                Year year = isYear(test);
                                if (year == null) {
                                    System.out.println("The string you entered is not a correct year format");
                                    break;
                                }
                                ArrayList<Movie> moviesByReleaseYear = Movie.findMoviesWithSpecificReleaseYear(year);
                                if (moviesByReleaseYear == null) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie2 : moviesByReleaseYear) {
                                    System.out.printf("%d. %s\n", i, movie2.getTitle());
                                    i++;
                                }
                                System.out.println("choose a movie to see more about");
                                //add movie functionality
                                if (isInteger(choice2 = scanner.nextLine()))
                                    movie = moviesByReleaseYear.get(Integer.parseInt(choice2) - 1);
                                if (movie != null)
                                    user.movieFunctionality(movie);

                                break;
                            case 12:
                                recommendation = user.recomendMoviesGenre();
                                if (recommendation.isEmpty()) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie1 : recommendation) {
                                    movie1.viewMovie();
                                }
                                break;
                            case 13:
                                recommendation = user.recomendMoviesLanguage();
                                if (recommendation.isEmpty()) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie1 : recommendation) {
                                    movie1.viewMovie();
                                }
                                break;
                            case 14:
                                recommendation = user.recomendMoviesCast();
                                if (recommendation.isEmpty()) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie1 : recommendation) {
                                    movie1.viewMovie();
                                }
                                break;
                            case 15:
                                recommendation = Movie.findPopularMovies();
                                if (recommendation.isEmpty()) {
                                    System.out.println("This list is empty");
                                    break;
                                }
                                for (Movie movie1 : recommendation) {
                                    movie1.viewMovie();
                                }
                                break;
                            case 16:
                                user.postFunctionality();
                                break;
                            case 17:
                                user.directFunctionality();
                                break;
                            case 18:
                                user.groupFunctionality();
                                break;
                            default:
                                System.out.println("The character you entered is not a valid option");
                        }
                    }
                    //admin homepage
                    else if (user.getUserType() == UserType.ADMIN) {
                        System.out.printf("welcome admin: %s\n", user.userName);
                        while ((loginPageChoice != 0) ) {
                            System.out.println("0. Exit this account");
                            System.out.println("1. Adding admin setting");
                            System.out.println("2. Movie setting");
                            System.out.println("3. Review setting");
                            System.out.println("4. People setting");
                            System.out.println("5. Users setting");
                            System.out.println("6. Edits setting");
                            System.out.println("7. Acting Records setting");
                            System.out.println("8. Make Editor");
//                            System.out.println("9. Go to my normal account home page");
                            if (isInteger(test = scanner.nextLine()))
                                loginPageChoice = Integer.parseInt(test);
                            test = "";
                            switch (loginPageChoice) {
                                case 0:
                                    break;
                                case 1:
                                    UserType userType;
                                    System.out.println("1. make a user admin\n2.make a user editor\n3.Exit");
                                    while ((!(test = scanner.nextLine()).equals("1")) && (!test.equals("2")) && (!test.equals("3")))
                                        System.out.println("The character you entered is not a valid option");
                                    if (test.equals("3")) break;
                                    if (test.equals("1")) userType = UserType.ADMIN;
                                    else userType = UserType.EDITOR;
                                    //this method asks for user info and everything inside
                                    user.changeUserConfiguration(userType);
                                    break;
                                case 2:
                                    user.adminMovieSetting();
                                    break;
                                case 3:
                                    user.adminReviewSetting();
                                    break;
                                case 4:
                                    user.adminPeopleSetting();
                                    break;
                                case 5:
                                    user.adminUsersSetting();
                                    break;
                                case 6:
                                    user.adminEditSetting();
                                    break;
                                case 7:
                                    user.adminActingRecordSetting();
                                    break;
                                case 8:
                                    user.makeEditorFunctionality();
                                    break;
//                                case 9:
//                                    flag = true;
//                                    break;
                                default:
                                    System.out.println("the number you entered is not a valid option");
                            }
                        }
                    }
                    //editor homepage
                    else if (user.getUserType() == UserType.EDITOR) {
//                        System.out.println("Enter 1 to go to normal account home page and any other character to proceed to editor account");
//                        if (scanner.nextLine().equals("1")) {
//                            flag = true;
//                        } else
                            user.publishEdit();

                    }
                }
            }


        }

    }

}
