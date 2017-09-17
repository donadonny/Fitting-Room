package tk.talcharnes.unborify;

/**
 * Created by Tal on 9/16/2017.
 */

public class ContactUsObject {
    private String Message;
    private String Contact_type;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_type() {
        return Contact_type;
    }

    public void setContact_type(String contact_type) {
        Contact_type = contact_type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
