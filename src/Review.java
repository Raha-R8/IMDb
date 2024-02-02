import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.Serializable;

public class Review implements Serializable{
    private String content;
    private boolean isSpoiler;
    private float totalRate;
    private LocalDate contributionDay;
    //if the review is not a reply then this attribute is set to null
    private Review repliedTo;
    private User author;
    private ArrayList<User> raters;
    private Movie movie;
    //****************************************************
    //getters and setters
    //****************************************************
    //constructor


    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Review(){};

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSpoiler() {
        return isSpoiler;
    }

    public void setSpoiler(boolean spoiler) {
        isSpoiler = spoiler;
    }

    public float getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(float totalRate) {
        this.totalRate = totalRate;
    }

    public LocalDate getContributionDay() {
        return contributionDay;
    }

    public void setContributionDay(LocalDate contributionDay) {
        this.contributionDay = contributionDay;
    }

    public Review getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(Review repliedTo) {
        this.repliedTo = repliedTo;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public ArrayList<User> getRaters() {
        return raters;
    }

    public void setRaters(ArrayList<User> raters) {
        this.raters = raters;
    }

    //*********************************************************

    //1: deleted successfully
    //0: access denied
    public int deleteReview(User admin){
        if(admin.getUserType()==UserType.ADMIN){
            this.movie.getReviews().remove(this);
//            System.out.printf("Review successfully deleted by admin: %s\n",admin.getUserName());
            return 1;
        }
//        System.out.printf("Access denied: User %s is not an admin.\n",admin.getUserName());
        return 0;
    }

    //0: user already rated this review
    //1: user successfully rated this review
    public int rateReview(User user,boolean isHelpful){
        if(this.raters.contains(user)){
            return 0;
        }
        if(isHelpful)
             this.setTotalRate(this.getTotalRate()+5);
        else
            this.setTotalRate(this.getTotalRate()-5);
        //adding the user to the reviews raters
        this.raters.add(user);
        return 1;
    }
    public ArrayList<Review> showReplies(){
        ArrayList<Review> reviews = this.getMovie().getReviews();
        ArrayList<Review> replies = new ArrayList<>();
        int i=1;
        for (Review review:reviews) {
            if(review.repliedTo==this){
                replies.add(review);
                System.out.printf("%d. publish day: %s\nAuthor: %s\nContent: %s\n",i,review.contributionDay,review.author.getUserName(),review.content);
                i++;
            }
        }
        if(i==1)
            System.out.println("This review does not have any replies.");
    return replies;
    }

}
