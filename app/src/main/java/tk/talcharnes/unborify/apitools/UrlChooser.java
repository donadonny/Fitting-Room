package tk.talcharnes.unborify.apitools;

import android.util.Log;

import java.util.Random;

/**
 * Created by Tal on 4/15/2017.
 */

public class UrlChooser {
    private String url;
    private String[] urlArray;
    private int randomInt;
    private final String LOG_TAG = UrlChooser.class.getSimpleName();
    // TODO: 4/15/2017  future version will have an arraylist of urls that are created from URL sources that are active in
     //TODO: the options menu. These will be chosen by random in the getUrl method.


    public String getUrl() {
        randomInt = new Random().nextInt(2);
        Log.d(LOG_TAG, "Random int = " + randomInt);

        switch(randomInt){
            case 0:
//               Chuck Norris Jokes API
                urlArray = new String[]{"https://api.chucknorris.io/", "jokes/random"};
                break;
            case 1:
//              Random Jokes API
                urlArray = new String[]{"http://tambal.azurewebsites.net/", "/joke/random"};
        }


        url = urlArray[0];
        return url;
    }
    public String getUrlEnding(){
        return urlArray[1];
    }

    public int getRandomInt(){
        return randomInt;
    }
}
