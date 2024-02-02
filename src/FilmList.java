import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.io.Serializable;

public class FilmList implements Serializable{
    String name;
    private ListType listType;
    private LocalDate creationDate;
    private Map<String,Movie> movies;
    //****************************************************
    //getters and setters

    public ListType getListType() {
        return listType;
    }

    public void setListType(ListType listType) {
        this.listType = listType;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Map<String, Movie> getMovies() {
        return movies;
    }

    public void setMovies(Map<String, Movie> movies) {
        this.movies = movies;
    }

    public FilmList(String name, ListType listType, LocalDate creationDate, Map<String, Movie> movies) {
        this.name = name;
        this.listType = listType;
        this.creationDate = creationDate;
        this.movies = movies;
    }

    //check the user
    //check the list of movies to not contain the movie
   //returns 0 if the movie is already in the list


//    this one should not be needed here it can be done without writing the method
//    public void deleteMovieFromList(String title){
//        this.movies.remove(title);
//    }
    public void showMovies(){
        int i = 1;
        Collection<Movie> movies1 = this.getMovies().values();
        for (Movie movie: movies1) {
            System.out.printf("%d. %s\n",i,movie.getTitle());
            i++;
        }
    }

}
