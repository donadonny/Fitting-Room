package tk.talcharnes.unborify;

/**
 * Created by Tal on 6/29/2017.
 */

public class Photo {
    private long likes;
    private long dislikes;
    private String url;
    private long reports;
    private String user;
    private String occasion_subtitle;
    public final static String OCCASION_SUBTITLE_KEY = "occasion_subtitle";
    public final static String USER_KEY = "user";
    public final static String REPORTS_KEY = "reports";
    public static final String URL_KEY = "url";
    public static final String DISLIKES_KEY = "dislikes";
    public static final String LIKES_KEY = "likes";



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
}
