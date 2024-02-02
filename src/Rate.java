import java.io.Serializable;
public class Rate implements Serializable{
    private RateType type;
    private User rater;
    private Movie movie;
    //****************************************************

    public Rate(RateType type, User rater, Movie movie) {
        this.type = type;
        this.rater = rater;
        this.movie = movie;
    }


    //getters and setters

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public RateType getType() {
        return type;
    }

    public void setType(RateType type) {
        this.type = type;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }
    //************************************************
    public int getRateInt(){
        switch (this.getType()){
            case ONESTAR:
                return 1;
            case TWOSTAR:
                return 2;
            case THREESTAR:
                return 3;
            case FOURSTAR:
                return 4;
            case FIVESTAR:
                return 5;
        }
        return 0;
    }

}
