import javax.xml.crypto.Data;
import java.util.*;
import java.io.Serializable;
public class Language implements Serializable{
    String language;
    ArrayList<Movie> movies;
    //****************************************************
    //getters and setters

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private Language(String language) {
        this.language = language;
        this.movies = new ArrayList<Movie>();
    }
    public static Language addLanguage(String languageStr){
        Language language = new Language(languageStr);
        DataBase.getLanguages().put(languageStr,language);
        return language;
    }

    public static HashMap<Integer,Language> showLanguages(){
        HashMap<Integer,Language> languageHashMap = new HashMap<>();
        int i = 1;
        Collection<Language> languages = DataBase.getLanguages().values();
        if(languages.isEmpty()){
            System.out.println("This list is empty");
            return null;
        }
        for(Language language: languages) {
            System.out.printf("%d. %s\n",i,language.getLanguage());
            languageHashMap.put(i,language);
            i++;
        }
        return languageHashMap;
    }

}
