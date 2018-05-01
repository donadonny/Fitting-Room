package tk.talcharnes.unborify.Models;

/**
 * Created by Khuram Chaudhry on 9/21/17.
 *
 * This class is an object that holds information about the users.
 *
 * Properties:
 *      name - holds the full name of the user.
 *      email - holds the email address of the user.
 *      uri - holds the uri path of user's profile photo, may be null.
 *      dateJoined - holds a string representation of the time user joined the service.
 */

public class UserModel {

    public final static String NAME_KEY = "name";
    public final static String URI_KEY = "uri";

    private String name;
    private String email;
    private String uri;
    private long dateJoined;

    /**
     * Required empty Constructor for FireBase.
     */
    public UserModel() {

    }

    /**
     * Custom Constructor.
     */
    public UserModel(String name, String email, String uri, long dateJoined) {
        this.name = name;
        this.email = email;
        this.uri = uri;
        this.dateJoined = dateJoined;
    }

    /**
     * This class returns the user name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This class returns the email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * This class returns the string representation of the user profile photo uri.
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * This class returns the long representation of the time user joined.
     */
    public long getDateJoined() {
        return this.dateJoined;
    }

    /**
     * This class set name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This class set email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This class set uri property.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * This class set timeJoined property.
     */
    public void setDateJoined(long dateJoined) {
        this.dateJoined = dateJoined;
    }

}
