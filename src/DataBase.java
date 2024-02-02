import com.sun.javafx.scene.control.skin.VirtualFlow;
import sun.security.krb5.internal.HostAddress;

import javax.xml.crypto.Data;
import java.util.*;
import java.io.*;
public class DataBase {
    private static List<Movie> movieRating = new ArrayList<>();
    //for the next set the comparator should be release date which is of date class need to search
    private static List<Movie> newestMovies = new ArrayList<>();
    private static Map<String,Movie> movies = new HashMap<>();
    //the key should be username because it should be unique while passwords might and should be possibly the sameext
    private static Map<String,Person> peopleMap = new HashMap<>();
    private  static Map<String, User> userMap = new HashMap<>();


    //this array should be the string of languages be very careful when adding languages!!!!!!!!!!!!
    private static HashMap<String,Language> languages = new HashMap<>();
    private static HashMap<String,Occupation> occupationHashMap = new HashMap<>();

    private static HashMap<String,Genre> genreHashMap = new HashMap<>();
    private static List<ReviewEdit> reviewEdits = new ArrayList<>();
    private static List<MovieEdit> movieEdits = new ArrayList<>();
    private static List<BiographyEdit> biographyEdits = new ArrayList<>();
    //****************************************************
    //extra maps to simplify movies search and sorting
    //note : when working with hashmap the if the key value wouldn't be unique the result will be the lastly pushed value with that key
    //I think it actually replaces the previous ones
    //***************************************************
    //getters and setters

    public static List<ReviewEdit> getReviewEdits() {
        return reviewEdits;
    }
    public static List<MovieEdit> getMovieEdits() {
        return movieEdits;
    }

    public static List<BiographyEdit> getBiographyEdits() {
        return biographyEdits;
    }

    public static HashMap<String, Occupation> getOccupationHashMap() {
        return occupationHashMap;
    }


    public static HashMap<String, Genre> getGenreHashMap() {
        return genreHashMap;
    }


    public static List<Movie> getNewestMovies() {
        return newestMovies;
    }


    public static HashMap<String, Language> getLanguages() {
        return languages;
    }


    public static Map<String, Movie> getMovies() {
        return movies;
    }


    public static List<Movie> getMovieRating() {
        return movieRating;
    }


    public static Map<String, Person> getPeopleMap() {
        return peopleMap;
    }


    public static Map<String, User> getUserMap() {
        return userMap;
    }



    //*****************************************************


}
