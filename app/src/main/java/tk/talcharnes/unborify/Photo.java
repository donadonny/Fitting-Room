package tk.talcharnes.unborify;

import java.util.HashMap;

/**
 * Created by Tal on 6/29/2017.
 */

public class Photo {
    private long likes;
    private long dislikes;
    private String url;
    private HashMap<String, String> Votes;
    private long reports;
    private String user;
    private String occasion_subtitle;
    private int orientation;
    private boolean isAd;
    private String category;
    public final static String OCCASION_SUBTITLE_KEY = "occasion_subtitle";
    public final static String USER_KEY = "user";
    public final static String REPORTS_KEY = "reports";
    public static final String URL_KEY = "url";
    public static final String DISLIKES_KEY = "dislikes";
    public static final String LIKES_KEY = "likes";
    public static final String CATEGORY_KEY = "category";
    public static final String VOTES = "votes";

    public Photo() {

    }

    public Photo(String user, String occasion_subtitle, String category, long likes, long dislikes, long reports,
                 int orientation, String url) {
        this.user = user;
        this.occasion_subtitle = occasion_subtitle;
        this.category = category;
        this.likes = likes;
        this.dislikes = dislikes;
        this.reports = reports;
        this.orientation = orientation;
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public void setVotes(HashMap<String, String> Votes) {
        this.Votes = Votes;
    }

    public HashMap<String, String> getVotes() {
        return Votes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public long getReports() {
        return reports;
    }

    public void setReports(long reports) {
        this.reports = reports;
    }

    public String getOccasion_subtitle() {
        return occasion_subtitle;
    }

    public void setOccasion_subtitle(String occasion_subtitle) {
        this.occasion_subtitle = occasion_subtitle;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isAd() {
        return isAd;
    }

    public void setAd(boolean ad) {
        isAd = ad;
    }

    public String getCategory() {
        if(category != null) {
            return category;
        }
        return "Fashion";
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
