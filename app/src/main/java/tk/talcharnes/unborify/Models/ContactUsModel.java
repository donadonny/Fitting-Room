package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 9/16/
 *
 * This class is an Object design for the 'Contact Us'.
 *
 * Properties:
 *      message - this property holds the message user wants to send to the company.
 *      contactType - this property tells what reason the user is contacting for, this can contain
 *                      string that says bug.
 *      email - this property holds the email the user wants to be contacted by, may be the same
 *                  email in the UserModel.
 */

public class ContactUsModel {

    private String message;
    private String contactType;
    private String email;

    /**
     * Required empty Constructor for FireBase.
     */
    public ContactUsModel() {

    }

    /**
     * Default Constructor.
     */
    public ContactUsModel(String message, String contactType, String email) {
        this.message = message;
        this.contactType = contactType;
        this.email = email;
    }

    /**
     * This class returns the user message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * This class returns the contact type.
     */
    public String getContactType() {
        return contactType;
    }

    /**
     * This class return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * This class sets the message property with user message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This class sets the contactType property with the chosen string.
     */
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    /**
     * This class sets the email property with the provided email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
