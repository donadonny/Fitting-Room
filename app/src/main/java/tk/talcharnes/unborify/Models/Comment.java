package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 9/3/2017.
 */

public class Comment {
    private String commenter;
    private String commentString;
    private String date;
    private String photo_url;
    private String comment_key;
    private String photo_Uploader;


    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getComment_key() {
        return comment_key;
    }

    public void setComment_key(String comment_key) {
        this.comment_key = comment_key;
    }

    public String getPhoto_Uploader() {
        return photo_Uploader;
    }

    public void setPhoto_Uploader(String photo_Uploader) {
        this.photo_Uploader = photo_Uploader;
    }

}
