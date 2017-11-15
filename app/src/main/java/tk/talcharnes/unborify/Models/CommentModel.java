package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 9/3/2017.
 * This class is an Object design for comments.
 *
 * Properties:
 *      commenter - this property contains the user id of the commenter.
 *      commentString - this property contains the comment message.
 *      date - holds a string representation of the date of when the comment was created.
 *      photo_url - this property contains the id of the photo.
 *      comment_key - this property contains the id of the comment stored in the database.
 *      photo_uploader - this property contains the id of the user who uploaded the photo.
 */

public class CommentModel {

    private String commenter;
    private String commentString;
    private String date;
    private String photo_url;
    private String comment_key;
    private String photo_Uploader;

    /**
     * This class returns commenter uid.
     * */
    public String getCommenter() {
        return commenter;
    }

    /**
     * This class return the comment message.
     * */
    public String getCommentString() {
        return commentString;
    }

    /**
     * This class return the time the comment was made.
     * */
    public String getDate() {
        return date;
    }

    /**
     * This class returns the photo uid.
     * */
    public String getPhoto_url() {
        return photo_url;
    }

    /**
     * This class returns the comment uid.
     * */
    public String getComment_key() {
        return comment_key;
    }

    /**
     * This class returns the photo uploader uid.
     * */
    public String getPhoto_Uploader() {
        return photo_Uploader;
    }

    /**
     * This class sets commenter with the commenter uid.
     * */
    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    /**
     * This class sets commentString with the comment message.
     * */
    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    /**
     * This class sets the date property with time the comment was created.
     * */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This class sets photo_url with the photo uid.
     * */
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    /**
     * This class sets comment_key with the comment uid.
     * */
    public void setComment_key(String comment_key) {
        this.comment_key = comment_key;
    }

    /**
     * This class sets photo_Uploader with the photo uploader uid.
     * */
    public void setPhoto_Uploader(String photo_Uploader) {
        this.photo_Uploader = photo_Uploader;
    }

}
