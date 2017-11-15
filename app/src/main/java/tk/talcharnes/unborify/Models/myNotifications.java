package tk.talcharnes.unborify.Models;

/**
 * Created by khuramchaudhry on 9/4/17.
 */

public class myNotifications {

    private boolean read;
    private String photoUrl;
    private String message;
    private String senderID;

    public myNotifications() {

    }

    public myNotifications(Boolean read, String photoUrl, String message, String senderID) {
        this.read = read;
        this.photoUrl = photoUrl;
        this.message = message;
        this.senderID = senderID;
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

}
