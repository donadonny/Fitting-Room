package tk.talcharnes.unborify;

/**
 * Created by Tal on 4/20/2017.
 */

public class Joke {
    private String joke;

    private int mediaType;
    public static int STRING_JOKE_TYPE = 0;
    public static int VIDEO_JOKE_TYPE = 1;
    public static int IMAGE_JOKE_TYPE = 2;


    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke, int mediaType) {
        this.joke = joke;
        this.mediaType = mediaType;
    }
}
