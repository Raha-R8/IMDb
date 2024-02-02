
import java.util.HashMap;
import java.util.List;

public class ReviewEdit extends Edit{
    Review review;
    //for review the only suggested edit will be inappropriate content
    //so the review will be deleted


    private ReviewEdit(User publisher, String targetDetail, String newDetail, Review review) {
        super(publisher, targetDetail, newDetail);
        this.review = review;
    }
    public static ReviewEdit addReviewEdit(User publisher, String targetDetail, String newDetail, Review review){
        ReviewEdit reviewEdit = new ReviewEdit(publisher,targetDetail,newDetail,review);
        DataBase.getReviewEdits().add(reviewEdit);
        publisher.getEdits().add(reviewEdit);
        return reviewEdit;
    }

    @Override
    int confirmEdit(User admin) {
        if(admin.getUserType()==UserType.ADMIN){
            this.confirmationStatus = 1;
            review.deleteReview(admin);
            return 1;
        }
        return 0;
    }

    @Override
    int rejectEdit(User admin) {
        if(admin.getUserType()==UserType.ADMIN){
            this.confirmationStatus = 2;
            return 1;
        }
        return 0;
    }
    public static HashMap<Integer,ReviewEdit> showReviewEdits(){
        HashMap<Integer,ReviewEdit> reviewEditHashMap = new HashMap<>();
        int i = 1;
        List<ReviewEdit> reviewEditList = DataBase.getReviewEdits();
        if(reviewEditList.isEmpty())
            return null;
        for(ReviewEdit reviewEdit: reviewEditList) {
            System.out.printf("%d. %s",i,reviewEdit.review.getContent());
            reviewEditHashMap.put(i,reviewEdit);
            i++;
        }
        return reviewEditHashMap;
    }
}
