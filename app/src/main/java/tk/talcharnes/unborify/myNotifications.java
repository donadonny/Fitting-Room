package tk.talcharnes.unborify;

/**
 * Created by khuramchaudhry on 9/4/17.
 *
 */

public class myNotifications {

    private boolean read;
    private String photoUrl;
    private String message;
    private String senderID;
    private String senderName;

    public myNotifications() {

    }

    public myNotifications(Boolean read, String photoUrl, String message, String senderID, String senderName) {
        this.read = read;
        this.photoUrl = photoUrl;
        this.message = message;
        this.senderID = senderID;
        this.senderName = senderName;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setPhotoUrl(String photo) {
        this.photoUrl = photo;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Boolean getRead() {
        return read;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getSenderName() {
        return senderName;
    }
}
