package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 9/16/2017.
 */

public class ContactUsObject {
    private String message, contact_type, email;

    public ContactUsObject() {

    }

    public ContactUsObject(String message, String contact_type, String email) {
        this.message = message;
        this.contact_type = contact_type;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
