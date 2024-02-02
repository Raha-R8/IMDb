import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MovieEdit extends Edit{
    Movie movie;
    private MovieEdit(Movie movie,User publisher, String targetDetail, String newDetail) {
        super(publisher, targetDetail, newDetail);
        this.movie = movie;
    }
    public static void addMovieEdit(Movie movie ,User publisher, String targetDetail, String newDetail){
        MovieEdit edit = new MovieEdit(movie,publisher,targetDetail,newDetail);
        DataBase.getMovieEdits().add(edit);
        publisher.getEdits().add(edit);
    }
    @Override
    int confirmEdit(User admin){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Language language;

        if(admin.getUserType()==UserType.ADMIN) {
            //setting the status to true
            this.confirmationStatus = 1;
            //applying the change
            switch (this.targetDetail) {
                case "title":
                    this.movie.setTitle(admin, newDetail);
                    break;
                case "picturePath":
                    this.movie.setPicturePath(admin, newDetail);
                    break;
                case "trailerPath":
                    this.movie.setTrailerPath(admin, newDetail);
                    break;
                case "movieDescription":
                    this.movie.setMovieDescription(admin, newDetail);
                    break;
                case "budget":
                    this.movie.setBudget(admin, Integer.parseInt(newDetail));
                    break;
                case "releaseDate":
                    //errors of input should be handled before it gets here
                    this.movie.setReleaseDate(admin, LocalDate.parse(newDetail, formatter));
                    //should sort the newest movies arraylist again
                    DataBase.getNewestMovies().sort(Movie.dateComparator);
                    break;
                case "language":
                    if ((language = DataBase.getLanguages().get(newDetail))==null){
                        language = Language.addLanguage(newDetail);
                    }
                    this.movie.getLanguage().getMovies().remove(this.movie);
                    this.movie.setLanguage(admin,language);
                    language.getMovies().add(this.movie);
                    break;
            }
            return 1;
        }
        return 0;
    }

    @Override
    int rejectEdit(User admin) {
        if(admin.getUserType()==UserType.ADMIN){
            confirmationStatus = 2;
            return 1;
        }
        return 0;
    }
    public static HashMap<Integer,MovieEdit> showMovieEdits(){
        HashMap<Integer,MovieEdit> movieEditHashMap = new HashMap<>();
        List<MovieEdit> movieEdits = DataBase.getMovieEdits();
        int i = 1;
        if(movieEdits.isEmpty())
            return null;
        for (MovieEdit movieEdit:movieEdits) {
            System.out.printf("%d. detail to edit: %s\nnew offered detail: %s\n",i,movieEdit.getTargetDetail(),movieEdit.getNewDetail());
            movieEditHashMap.put(i,movieEdit);
            i++;
        }
        return movieEditHashMap;
    }
}
