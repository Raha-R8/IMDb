import java.io.Serializable;
public class Genre implements Serializable{
    String genreName;
    //****************************************************
    //getters and setters


    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    private Genre(String genreName) {
        this.genreName = genreName;
        DataBase.getGenreHashMap().put(genreName, this);
    }
    public static Genre addGenre(String genreName){
        Genre genre = new Genre(genreName);
        DataBase.getGenreHashMap().put(genreName, genre);
        return genre;
    }
}
