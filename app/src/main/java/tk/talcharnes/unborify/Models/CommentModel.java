package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 9/3/2017.
 *
 * This class is an Object design for comments.
 *
 * Properties:
 *      commenterUid - this property contains the user id of the commenter.
 *      commentMessage - this property contains the comment message.
 *      date - holds a string representation of the date of when the comment was created.
 *      photoUrl - this property contains the id of the photo.
 *      commentKey - this property contains the id of the comment stored in the database.
 *      photoUploaderUid - this property contains the uid of the user who uploaded the photo.
 */

public class CommentModel {

    private String commenterUid;
    private String commentMessage;
    private String date;
    private String photoUrl;
    private String commentKey;
    private String photoUploaderUid;

    /**
     * Required empty Constructor for FireBase.
     */
    public CommentModel() {

    }

    /**
     * Default Constructor.
     */
    public CommentModel(String commenterUid, String commentMessage, String date, String photoUrl,
                        String commentKey, String photoUploaderUid) {
        this.commenterUid = commenterUid;
        this.commentMessage = commentMessage;
        this.date = date;
        this.photoUrl = photoUrl;
        this.commentKey = commentKey;
        this.photoUploaderUid = photoUploaderUid;
    }

    /**
     * This class returns commenter uid.
     */
    public String getCommenterUid() {
        return commenterUid;
    }

    /**
     * This class return the comment message.
     */
    public String getCommentString() {
        return commentMessage;
    }

    /**
     * This class return the time the comment was made.
     */
    public String getDate() {
        return date;
    }

    /**
     * This class returns the photo uid.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * This class returns the comment primary key.
     */
    public String getCommentKey() {
        return commentKey;
    }

    /**
     * This class returns the photo uploader uid.
     */
    public String getPhotoUploaderUid() {
        return photoUploaderUid;
    }

    /**
     * This class sets commenterUid with the commenter uid.
     */
    public void setCommenterUid(String commenterUid) {
        this.commenterUid = commenterUid;
    }

    /**
     * This class sets commentMessage with the comment message.
     */
    public void setCommentString(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    /**
     * This class sets the date property with time the comment was created.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This class sets photoUrl with the photo uid.
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * This class sets commentKey with the comment primary key in the database.
     */
    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    /**
     * This class sets photoUploaderUid with the photo uploader uid.
     */
    public void setPhotoUploaderUid(String photoUploaderUid) {
        this.photoUploaderUid = photoUploaderUid;
    }

}