package tk.talcharnes.unborify;

import android.net.Uri;

/**
 * Created by khuramchaudhry on 9/21/17.
 */

public class User {

    private String name, email, date_joined;
    private String uri;

    public User() {

    }

    public User(String name, String email, String uri, String date_joined) {
        this.name = name;
        this.email = email;
        this.uri = uri;
        this.date_joined = date_joined;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUri() {
        return this.uri;
    }

    public String getDate_joined() {
        return this.date_joined;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }
}
