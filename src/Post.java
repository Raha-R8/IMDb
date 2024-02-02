import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Post {
    String content;
    String picturePath;
    String videoPath;
    User author;
    Post repliedTo;
    Map<User,Post> replies;

    public Post(String content, String picturePath, String videoPath, User author, Post repliedTo) {
        this.content = content;
        this.picturePath = picturePath;
        this.videoPath = videoPath;
        this.author = author;
        this.repliedTo = repliedTo;
        this.replies = new HashMap<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(Post repliedTo) {
        this.repliedTo = repliedTo;
    }

    public Map<User, Post> getReplies() {
        return replies;
    }

    public void setReplies(Map<User, Post> replies) {
        this.replies = replies;
    }
    public static void addPost(String content, String picturePath, String videoPath, User author, Post repliedTo){
        Post post = new Post(content,picturePath,videoPath,author,repliedTo);
        author.getPosts().add(post);
    }
    public void showPost(){
        System.out.println("author: "+this.author.getUserName());
        System.out.println("content: "+this.content);
        System.out.println("picture path: "+this.picturePath);
        System.out.println("video path: "+this.videoPath);
        System.out.println("replies");
        Collection<Post> replies = this.replies.values();
        for (Post post: replies) {
            System.out.println("author: "+post.author);
            System.out.println("content: "+post.content);
            System.out.println("picture path: "+post.picturePath);
            System.out.println("video path: "+post.videoPath);
        }
    }

}
