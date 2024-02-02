import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
//        System.out.println(Gender.MALE);

//        DataBase.loadDataFromFile("data.db");
        DataBase dataBase = new DataBase();
        User admin = User.makeFirstAdmin();
        User user1 = User.signUp("raha","raha123");
        User user2 = User.signUp("moh","moh123");
        User editor  = User.signUp("editor","editor");
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 2, 1);
        Genre genre1 = Genre.addGenre("scary");
        Genre genre2 = Genre.addGenre("comedy");
        Genre genre3 = Genre.addGenre("action");
        ArrayList<Genre> genres1 = new ArrayList<>();
        genres1.add(genre1);genres1.add(genre2);
        ArrayList<Genre> genres2 = new ArrayList<>();
        genres2.add(genre1);genres2.add(genre3);
        Language language1 = Language.addLanguage("persian");
        Language language2 = Language.addLanguage("english");
        Movie movie1 = Movie.addMovie(admin,"shahre hert","eiofje","ofjfie","this is the shitiest movie ever",date1,1200,genres1,language1);
        Movie movie2 = Movie.addMovie(admin,"kung fu panda","eioewefje","ofjfddsie","this is the best movie ever",date2,20000,genres2,language2);
        Person person1 = Person.createPerson(admin,"angelina","july",43,"1233322111",Gender.FEMALE,new HashMap<>());
        Person person2 = Person.createPerson(admin,"brad","pit",46,"1333322111",Gender.MALE,new HashMap<>());
        movie1.addReview(null,user1,"hello",false);
        movie1.addReview(null,user2,"shiiiiti",true);
        admin.adminActingRecordSetting();
        User.loginPage();
//        DataBase.saveDataToFile("data.db");

    }
}