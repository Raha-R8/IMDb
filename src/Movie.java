import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.io.Serializable;

public class Movie implements Serializable{
    private String title;
    private String picturePath;
    private String trailerPath;
    private String movieDescription;
    private LocalDate releaseDate;
    private int budget;

    private ArrayList<Genre> genres;
    private Language language;
    private ArrayList<Review> reviews;
    //I might have to store rates both ways so i can show movie rates to people and also
    //show each user their individual rate for each movie
    //private ArrayList<Rate> rates;
    private ArrayList<ActingRecord> actingRecords;
    //I'm going to keep this for later that i will have to apply the edit classes
    //because I am yet to fully understand if these classes are needed when the only data they have is a connection
    // private MovieEdit[] movieEdits;
    private ArrayList<Rate> rates;
    private ArrayList<FilmList> filmLists;
    private int totalRate;
    //****************************************************************************
    //constructor

//  important ; acting records should be added later and will not be part of the constructor
    private Movie(String name, String picturePath, String trailerPath, String movieDescription, LocalDate releaseDate, int budget, ArrayList<Genre> genres, Language language) {
        this.title = name;
        this.picturePath = picturePath;
        this.trailerPath = trailerPath;
        this.movieDescription = movieDescription;
        this.releaseDate = releaseDate;
        this.budget = budget;
        this.genres = genres;
        this.language = language;
        //these three will be null when a movie is first created and will be filled later on
        this.reviews = new ArrayList<>();
        this.rates = new ArrayList<>();
        this.totalRate = 0;
        this.actingRecords = new ArrayList<>();
        this.filmLists = new ArrayList<>();
    }
    //****************************************************************************
    //Getters and setters


    public ArrayList<FilmList> getFilmLists() {
        return filmLists;
    }

    public void setFilmLists(ArrayList<FilmList> filmLists) {
        this.filmLists = filmLists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(User admin, String title) {
        if (admin.getUserType() == UserType.ADMIN)
            this.title = title;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(User admin, String picturePath) {
        if (admin.getUserType() == UserType.ADMIN)
            this.picturePath = picturePath;
    }

    public String getTrailerPath() {
        return trailerPath;
    }

    public void setTrailerPath(User admin, String trailerPath) {
        if (admin.getUserType() == UserType.ADMIN)
            this.trailerPath = trailerPath;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(User admin, String movieDescription) {
        if (admin.getUserType() == UserType.ADMIN)
            this.movieDescription = movieDescription;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(User admin, LocalDate releaseDate) {
        if (admin.getUserType() == UserType.ADMIN)
            this.releaseDate = releaseDate;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(User admin, int budget) {
        if (admin.getUserType() == UserType.ADMIN)
            this.budget = budget;
    }


    public Language getLanguage() {
        return language;
    }

    public void setLanguage(User admin, Language language) {
        if (admin.getUserType() == UserType.ADMIN)
            this.language = language;
    }


    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(User admin, ArrayList<Genre> genres) {
        if (admin.getUserType() == UserType.ADMIN)
            this.genres = genres;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(User admin, ArrayList<Review> reviews) {
        if (admin.getUserType() == UserType.ADMIN)
            this.reviews = reviews;
    }

//    public ArrayList<Rate> getRates() {
//        return rates;
//    }
//
//    public void setRates(User admin,ArrayList<Rate> rates) {
//        if(admin.getUserType()==UserType.ADMIN)
//        this.rates = rates;
//    }

    public ArrayList<ActingRecord> getActingRecords() {
        return actingRecords;
    }

//    public void setActingRecords(User admin,ArrayList<ActingRecord> actingRecords) {
//        if(admin.getUserType()==UserType.ADMIN)
//        this.actingRecords = actingRecords;
//    }

    public ArrayList<Rate> getRates() {
        return rates;
    }

//    public void setRates(ArrayList<Rate> rates) {
//        this.rates = rates;
//    }

    public int getTotalRate() {
        return totalRate;
    }

    //****************************************************************************
    //_____________________________________________________      database      ____________________________________________________________
    //other methods
    //adding and deleting from data base
    //data base
    private void addMovieToDB() {
        DataBase.getMovies().put(this.getTitle(), this);
    }

    public static Movie searchMovieInDB(String title) {
        return DataBase.getMovies().get(title);
    }


    private void deleteMovieFromDB() {
        DataBase.getMovies().remove(this.getTitle(), this);
    }
    //************************************
    //public methods for changes in movies
    //return codes:,,
    //1: movie was added successfully 2:movie already exists 0:access denied

    public static Movie addMovie(User admin, String title, String picturePath, String trailerPath, String movieDescription, LocalDate releaseDate, int budget, ArrayList<Genre> genres, Language language) {
        if (admin.getUserType() == UserType.ADMIN) {
            if (Movie.searchMovieInDB(title) == null) {
                Movie movie = new Movie(title, picturePath, trailerPath, movieDescription, releaseDate, budget, genres, language);
                //movie is added to db
                movie.addMovieToDB();
                //movie should be added to the highest rated movies in db and also the newest movies
                DataBase.getMovieRating().add(movie);
                DataBase.getMovieRating().sort(Movie.ratingComparator);
                DataBase.getNewestMovies().add(movie);
                DataBase.getNewestMovies().sort(Movie.dateComparator);
                return movie;
            }
        }
        return null;
    }
    //return codes:
    //1: movie was deleted successfully 2:movie doesn't exist 0:access denied


    public int deleteMovie(User admin) {
        if (admin.getUserType() == UserType.ADMIN) {
            if (Movie.searchMovieInDB(this.getTitle()) != null) {
                this.deleteMovieFromDB();
                //removing and adjusting the database records
                DataBase.getMovieRating().remove(this);
                DataBase.getMovieRating().sort(Movie.ratingComparator);
                DataBase.getNewestMovies().remove(this);
                DataBase.getNewestMovies().sort(Movie.dateComparator);
                //removing the movie from all the film lists that it is in them
                for (FilmList filmlist:this.filmLists) {
                    filmlist.getMovies().remove(this.getTitle());
                }
                return 1;
            }
            return 2;
        }
        return 0;
    }

    //*****************************************
    //methods regarding rates of a movie
    //changing or adding rate to a movie
    //this method automatically changes the rate if the rate exists and the user rates again
    //_____________________________________________________      rate      ____________________________________________________________


    public static Comparator<Movie> ratingComparator = Comparator.comparingDouble(Movie::getTotalRate).reversed();
    public static Comparator<Movie> dateComparator = Comparator.comparing(Movie::getReleaseDate).reversed();

    public void rate(User user, RateType rateType) {

        //checking if user has already rated this movie
        Rate rate;
        if ((rate = user.searchRates(this)) == null) {
            rate = new Rate(rateType, user, this);
            user.getRates().add(rate);
            this.rates.add(rate);
        } else {
            //the rate instance already exists, so I should only change its content to the new content
            this.totalRate -= rate.getRateInt();
            rate.setType(rateType);
        }
        this.totalRate += rate.getRateInt();
        //because the movies rating has changed now the list must get sorted again
        DataBase.getMovieRating().sort(Movie.ratingComparator);
    }

    //deleting the rate of a user
    public void deleteRate(User user) {
        //checking if user has already rated this movie
        Rate rate;
        if ((rate = user.searchRates(this)) != null) {
            this.rates.remove(rate);
            user.getRates().remove(rate);
            this.totalRate -= rate.getRateInt();
        }
        //if the user hasn't rated this movie nothing will change
        //sorting the rated movie list again after retracting a vote
        DataBase.getMovieRating().sort(Movie.ratingComparator);
    }

    //finding the total rate of a movie
    public float movieRate() {
        int size;
        if((size = this.rates.size())==0)
            return 0;
        return this.totalRate / (float) size;
    }

    //_____________________________________________________      review      ____________________________________________________________
    public Review addReview(Review repliedTo, User author, String content, boolean isSpoiler) {
        Review review = new Review();
        review.setMovie(this);
        review.setAuthor(author);
        review.setTotalRate(0);
        review.setContributionDay(LocalDate.now());
        review.setContent(content);
        review.setSpoiler(isSpoiler);
        review.setRepliedTo(repliedTo);
        review.setRaters(new ArrayList<>());
        //adding review to the movie reviews
        this.getReviews().add(review);
        return review;
    }



    //this method may evolve to showing and adding replies and also rating reviews at the same time
    public int showReviews() {
        ArrayList<Review> reviews = this.reviews;
        int i = 1;
        int len = reviews.size();
        for (Review review : reviews) {
            System.out.printf("%d. publish day: %s\nAuthor: %s\nContent: %s\n", i, review.getContributionDay(), review.getAuthor().getUserName(), review.getContent());
            i++;
        }
        return i;
    }

    //_____________________________________________________      acting record      ____________________________________________________________

    public int showActingRecords() {
        int i = 1;
        for (ActingRecord actingRecord : this.actingRecords) {
            System.out.printf("%d. %s\nRole on set:%s\nPayment: %f\n",i, actingRecord.getCrewMember().getName() +" "+ actingRecord.getCrewMember().getLastname(), actingRecord.getJobOnSet().occupation, actingRecord.getPayment());
            i++;
        }

        return i;
    }

    //_____________________________________________________      public static searches      ____________________________________________________________
    //for genre I can just simply get the movies in that genre using genre.get()
    //same goes for language
    //or
    //the bellow method
    //this method looks among the highest rated movies and checks them to see if they have all of the chosen genres and if so
    //it will return an array list of those movies also the array list will be sorted based on the movie rate
    public static ArrayList<Movie> findMoviesWithSpecificGenres(ArrayList<Genre> genres) {
        ArrayList<Movie> hasGenre = new ArrayList<>();
        //looks through the highest rated movies and shows the movie if it has, all of the selected genres
        for (Movie movie : DataBase.getMovieRating()) {
            for (Genre genre : genres) {
                if (movie.getGenres().contains(genre)) {
                    hasGenre.add(movie);
                    break;
                }
            }
        }
        return hasGenre;
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //it is a bit different for date class I need to search
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    //this method also returns movies of a chosen language sorted based on rating

    public static ArrayList<Movie> findMoviesWithSpecificLanguage(Language language) {
        ArrayList<Movie> isInLanguage = new ArrayList<>();
        //looks through the highest rated movies and shows the movie if it has, all of the selected genres
        for (Movie movie : DataBase.getMovieRating()) {
            if (movie.getLanguage() == language)
                isInLanguage.add(movie);
        }
        return isInLanguage;
    }
    public static ArrayList<Movie> findMoviesWithSpecificReleaseYear(Year year) {
        ArrayList<Movie> isReleaseYear = new ArrayList<>();
        Collection<Movie> movies = DataBase.getMovies().values();
        for (Movie movie : movies) {
            if (movie.getReleaseDate().getYear() == year.getValue())
                isReleaseYear.add(movie);
        }
        return isReleaseYear;
    }
    public static ArrayList<Movie> findPopularMovies(){
       List<Movie> highestRatedMovies = DataBase.getMovieRating();
       Collection<User> users = DataBase.getUserMap().values();
       ArrayList<Movie> popularMovies = new ArrayList<>();
       int count = 0;
        for (Movie movie:highestRatedMovies) {
            for (User user:users ) {
                if(user.getFilmLists().get("favorites").getMovies().containsKey(movie.getTitle())){
                    count++;
                }
                if(count>30){
                    popularMovies.add(movie);
                    break;
                }
            }
        }
        return popularMovies;
    }


//_____________________________________________________      public static shows      ____________________________________________________________

    public static HashMap<Integer,Movie> showNewestMovies() {
        HashMap<Integer,Movie> newestMoviesMap = new HashMap<>();
        int i = 1;
        List<Movie> movies = DataBase.getNewestMovies();
        if(movies.isEmpty()) {
            System.out.println("This list is empty");
            return null;
        }
        for (Movie movie: movies) {
            System.out.printf("%d. %s\n",i,movie.getTitle());
            newestMoviesMap.put(i,movie);
            i++;
            if(i==21) break;
        }
        return newestMoviesMap;
    }
    public static HashMap<Integer,Movie> show250TopMovies() {
        HashMap<Integer,Movie> topMovieHashmap = new HashMap<>();
        int i = 1;
        List<Movie> movies = DataBase.getMovieRating();
        if(movies.isEmpty()){
            System.out.println("This list is empty");
            return null;
        }
        for (Movie movie: movies) {
            System.out.printf("%d. %s\n",i,movie.getTitle());
            topMovieHashmap.put(i,movie);
            i++;
            if(i==251) break;
        }
        return topMovieHashmap;
    }
    public void viewMovie(){
        System.out.println("Title: " +this.getTitle());
        System.out.println("Rating: "+this.movieRate());
        System.out.println("Language: "+ this.getLanguage().getLanguage());
        System.out.println("Picture: (because it is commandline app only the path will be shown here"+ this.getPicturePath());
        System.out.println("Trailer: (because it is commandline app only the path will be shown here"+ this.getTrailerPath());
        System.out.println("Budget: "+ this.getBudget());
        System.out.println("Release Date: "+ this.getReleaseDate());
        System.out.println("Movie description: "+ this.getMovieDescription());

    }

}



























//ts