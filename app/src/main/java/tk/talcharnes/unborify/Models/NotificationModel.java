package tk.talcharnes.unborify.Models;

/**
 * Created by Khuram Chaudhry on 9/4/17.
 *
 * This class is an object design for notifications to be used in FireBase DataBase and FireBase
 *      Functions.
 *
 * Properties:
 *      read - holds a boolean to tell if the user has seen the notification or not.
 *      photoUrl - holds the id the photo of which the notification pertains to.
 *      message - holds the reason of the notification, can be a comment message or something else.
 *      senderID - holds the uid of the user that cause the notification, may be a commenter on a
 *                  photo.
 */

public class NotificationModel {

    private boolean read;
    private String photoUrl;
    private String message;
    private String senderID;

    /**
     * Required empty Constructor for FireBase.
     */
    public NotificationModel() {

    }

    /**
     * Default Constructor.
     */
    public NotificationModel(Boolean read, String photoUrl, String message, String senderID) {
        this.read = read;
        this.photoUrl = photoUrl;
        this.message = message;
        this.senderID = senderID;
    }

    /**
     * This class returns the read property.
     */
    public Boolean getRead() {
        return read;
    }

    /**
     * This class returns the photo url.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * This class returns the message property.
     */
    public String getMessage() {
        return message;
    }

    /**
     * This class returns the sender uid.
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * This class sets the read property.
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * This class sets photoUrl with photo id.
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * This class sets the message property.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This class sets senderID property.
     */
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

}
