import java.time.LocalDate;
import java.io.Serializable;
public abstract class Edit implements Serializable{
    User publisher;
    String targetDetail;
    LocalDate publishDay;
    //the status will be 0 if the edits status is not confirmed
    // the status will be 1 if the edit is confirmed and 2 if rejected
    int confirmationStatus;
    String newDetail;
    //_____________________________________________________      constructor      ____________________________________________________________


    public Edit( User publisher,String targetDetail, String newDetail) {
        this.publisher = publisher;
        this.targetDetail = targetDetail;
        this.newDetail = newDetail;
        this.publishDay = LocalDate.now();
        this.confirmationStatus = 0;
    }
    //methods
    abstract int confirmEdit(User admin);
    abstract int rejectEdit(User admin);

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public String getTargetDetail() {
        return targetDetail;
    }

    public void setTargetDetail(String targetDetail) {
        this.targetDetail = targetDetail;
    }

    public LocalDate getPublishDay() {
        return publishDay;
    }

    public void setPublishDay(LocalDate publishDay) {
        this.publishDay = publishDay;
    }

    public int getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(int confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public String getNewDetail() {
        return newDetail;
    }

    public void setNewDetail(String newDetail) {
        this.newDetail = newDetail;
    }
}
