import java.util.*;
import java.io.Serializable;
public class Person implements Serializable{
    private String name;
    private String lastname;
    private int age;
    private String nationalID;

    //ContactPoint[] contactPoints;
    private Gender gender;
    //each person can follow another person
    private Map<String,ActingRecord> actingRecords;
    private ArrayList<Person> following;
    private ArrayList<User> followers;


    //********************************************************************
    //getter and setters:


    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }

    public Map<String, ActingRecord> getActingRecords() {
        return actingRecords;
    }

    public void setActingRecords(Map<String, ActingRecord> actingRecords) {
        this.actingRecords = actingRecords;
    }

    public ArrayList<Person> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<Person> following) {
        this.following = following;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }



    public void setGender(Gender gender) {
        this.gender = gender;
    }



    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    public String getNationalID() {
        return nationalID;
    }



    public Gender getGender() {
        return gender;
    }


    //**********************************************************************
    //constructor
    //default constructor is here because when new users are created their account will not have personal information set
    public Person(){};
    private Person(String name, String lastname, int age, String nationalID,  Gender gender,Map<String,ActingRecord> actingRecords) {
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.nationalID = nationalID;

        this.gender = gender;
        this.actingRecords = actingRecords;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
    }

    //**********************************************************************
    //other methods
    //method to sign up a person as a user
    //method to sign in a person as a user
    //data base
    public void addPerson(){
        DataBase.getPeopleMap().put(this.getNationalID(), this);
    }
    public void deletePerson(){
        DataBase.getPeopleMap().remove(this.getNationalID(),this);
        Collection<Movie> movies = DataBase.getMovies().values();
        ArrayList<User> followers = this.getFollowers();
        //removing the acting records of that person from all the movies that have it
        for (Movie movie:movies) {
            ArrayList<ActingRecord> actingRecords = movie.getActingRecords();
            for (ActingRecord  actingRecord: actingRecords) {
                if(actingRecord.getCrewMember()==this){
                    movie.getActingRecords().remove(actingRecord);
                }
            }
        }
        //removing the person from following list of all its followers
        for (Person person: followers) {
            person.getFollowing().remove(person);
        }
    }

    //this method creates a person like an actor,...
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //we have a problem here
    //because the user is not being created when creating a person, other people might make an account with the same username of the celebrity
    //and when later on I want to sign up that celeb using setOfficialAccount the user will not be signed up because it already is there with that username
    //and if I change it so that there would be a user added to the database when a new person is created then there will be this question that what is the
    //difference between a user and a person, and we wouldn't actually need a person class in the system. Fix it
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static Person createPerson(User admin,String name, String lastname, int age, String nationalID,  Gender gender,Map<String,ActingRecord> actingRecords){
        if(admin.getUserType()==UserType.ADMIN){
            Person person = new Person(name,lastname,age,nationalID,gender,actingRecords);
            person.addPerson();
            return person;
        }
        return null;
    }

    //this method sets an official account for a person like a celebrity that is already in the system
    //if return number is 2: The user already exists
    //if return number is 1: The user has successfully been created
    //if return number is 0: Access denied because the user trying to apply it is not admin
    public int setOfficialAccount(User admin){
        if(admin.getUserType()==UserType.ADMIN){
            String username = this.getName() + " "+ this.getLastname();
            User user ;
            user = User.signUp(username, this.getNationalID());
            if(user==null){
              return 2;
            }
            user.setPersonData(this.name, this.lastname, this.age,this.nationalID,this.gender,this.actingRecords,this.following);
            //deleting the existing person instance and replacing it with the one that has a user account
            this.deletePerson();
            user.addPerson();
            return 1;
        }
        return 0;
    }


    //_____________________________________________________      acting record      ____________________________________________________________
//i might have to chnage it from private

    public HashMap<Integer,ActingRecord> showActingRecords(){
        HashMap<Integer,ActingRecord> actingRecordHashMap = new HashMap<>();
        int i = 1;
        Collection<ActingRecord> actingRecordsCollection = this.actingRecords.values();
        if(actingRecordsCollection.isEmpty())
            return null;
        for (ActingRecord actingRecord : actingRecordsCollection) {
            System.out.printf("%d. Movie:%s\nRole on set:%s\nPayment: %f\n",i,actingRecord.getMovie().getTitle(),actingRecord.getJobOnSet().occupation,actingRecord.getPayment());
            actingRecordHashMap.put(i,actingRecord);
            i++;
        }
        return actingRecordHashMap;
    }
    //_____________________________________________________      acting record      ____________________________________________________________
    //returns a hashmap of the number and the national id of the person
    public static HashMap<Integer,Person> showPeople(){
        HashMap<Integer,Person> peoplemap = new HashMap<>();
        int i= 1;
        Collection<Person> people = DataBase.getPeopleMap().values();
        for (Person person:people) {
            System.out.printf("%d. %s\n",i,person.getName());
            peoplemap.put(i,person);
            i++;
        }
        return peoplemap;
    }

    public void viewPerson(){
        System.out.println(this.getName()+" "+ this.getLastname());
        System.out.println("Age: "+ this.getAge());
        System.out.println("Gender: "+ this.getGender());
        System.out.printf("Movies %s has contributed in:\n",this.getName());
        this.showActingRecords();
    }
}
