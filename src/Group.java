import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    String name;
    User admin;
    ArrayList<User> participants;
    HashMap<Post,User> posts;

    private Group(User admin,String name) {
        this.name = name;
        this.admin = admin;
        this.participants = new ArrayList<>();
        this.posts = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public HashMap<Post, User> getPosts() {
        return posts;
    }

    public void setPosts(HashMap<Post, User> posts) {
        this.posts = posts;
    }
}
