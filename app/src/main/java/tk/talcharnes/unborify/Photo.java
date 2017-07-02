package tk.talcharnes.unborify;

/**
 * Created by Tal on 6/29/2017.
 */

public class Photo {
    private int likes;
    private int dislikes;
    private String url;
    private int reports;
    private String user;
    private String occasion_subtitle;


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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public String getOccasion_subtitle() {
        return occasion_subtitle;
    }

    public void setOccasion_subtitle(String occasion_subtitle) {
        this.occasion_subtitle = occasion_subtitle;
    }
}
