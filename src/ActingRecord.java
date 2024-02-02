import java.util.Date;
import java.io.Serializable;
public class ActingRecord implements Serializable{
    private Person crewMember;
    private Movie movie;
    private Occupation jobOnSet;
    private double payment;

    //****************************************************
    //getters and setters


    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public Person getCrewMember() {
        return crewMember;
    }

    public void setCrewMember(Person crewMember) {
        this.crewMember = crewMember;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Occupation getJobOnSet() {
        return jobOnSet;
    }

    public void setJobOnSet(Occupation jobOnSet) {
        this.jobOnSet = jobOnSet;
    }


    //***********************************************************
    //constructor

    private ActingRecord(Person crewMember, Movie movie, Occupation jobOnSet, double payment) {
        this.crewMember = crewMember;
        this.movie = movie;
        this.jobOnSet = jobOnSet;
        this.payment = payment;
    }
    private String GetRecordKey(){
        return this.movie.getTitle() + ":" + this.jobOnSet.getOccupation();
    }

    //I want to be sure that when an acting record is created it is also added to both the movies acting record array and the crew members acting record array
    //0: access denied
    //1: acting record added successfully
    //2: this acting record already exists
    public static int addActingRecord(User admin, Person crewMember, Movie movie, Occupation jobOnSet, double payment){
        ActingRecord actingRecord;
        if(admin.getUserType()==UserType.ADMIN){
            //checking to see if an acting record with this information is already in the persons acting records
            //because every time an acting record is created it is automatically added to both movie and the persons acting records it is
            //enough to check it only on one side to make sure it is unique
            if(crewMember.getActingRecords().containsKey(movie.getTitle()+":"+jobOnSet.getOccupation()))
                return 2;
            actingRecord = new ActingRecord(crewMember, movie, jobOnSet,payment);
            crewMember.getActingRecords().put(actingRecord.GetRecordKey(),actingRecord);
            movie.getActingRecords().add(actingRecord);
            return 1;
        }
        return 0;
    }

    //0: access denied
    //1: removed or doesn't exist
    public int deleteActingRecord(User admin){
        if(admin.getUserType()==UserType.ADMIN){
            //the acting record will be deleted from both the persons acting record array and the movies acting record array
            this.getMovie().getActingRecords().remove(this);
            //Im using remove(key) because the keys are unique if they weren't then I would have had to also specify the object
            this.getCrewMember().getActingRecords().remove(this.GetRecordKey());
            return 1;
        }
        return 0;
    }
    //edit acting record information
    //this method should be filled later on when edit class is completed
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


}
