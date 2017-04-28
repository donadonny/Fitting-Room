
package tk.talcharnes.unborify.apitools.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RandomJokeApiModel {

    @SerializedName("joke")
    @Expose
    private String joke;

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

}
