package tk.talcharnes.unborify.apitools;

/**
 * Created by Tal on 4/15/2017.
 */

public class UrlChooser {
    private String url;
    // TODO: 4/15/2017  future version will have an arraylist of urls that are created from URL sources that are active in
     //TODO: the options menu. These will be chosen by random in the getUrl method.
    String movieQuotesApi = "https://andruxnet-random-famous-quotes.p.mashape.com/";
   String chuckNorrisApi = "https://api.chucknorris.io/jokes/random/";
//    String apiURL = "https://api.whatdoestrumpthink.com/";
//    String restOfURL = "api/v1/quotes/personalized?q=yourname";
//    String trumpdString;
    String[] urlArray = new String[]{"https://api.chucknorris.io/", "jokes/random"};
    public String getUrl() {
        url = urlArray[0];
        return url;
    }
    public String getUrlEnding(){
        return urlArray[1];
    }

}
