package tk.talcharnes.unborify.Models;

/**
 * Created by Tal on 6/29/2017.
 *
 * This object class represents photos.
 *
 * Properties:
 *      userUid - this holds the uid of the userUid who uploaded the photo.
 *      occasionSubtitle - this holds the occasion behind the photo.
 *      category - this holds the fashion category the photo relates to.
 *      likes - this holds number of likes.
 *      dislike - this holds number of dislikes.
 *      reports - this holds number of reports.
 *      orientation - this holds a integer representation of the photo orientation.
 *      url - this holds the url of photo uploaded to FireBase Storage, also the photo id.
 */

public class PhotoModel {

    public final static String USER_KEY = "userUid";
    public final static String OCCASION_SUBTITLE_KEY = "occasionSubtitle";
    public static final String CATEGORY_KEY = "category";
    public static final String LIKES_KEY = "likes";
    public static final String DISLIKES_KEY = "dislikes";
    public final static String REPORTS_KEY = "reports";
    public final static String ORIENTATION_KEY = "orientation";
    public static final String URL_KEY = "url";

    private String userUid;
    private String occasionSubtitle;
    private String category;
    private long likes;
    private long dislikes;
    private long reports;
    private int orientation;
    private String url;

    /**
     * Required empty Constructor for FireBase.
     */
    public PhotoModel() {

    }

    /**
     * Default Constructor.
     */
    public PhotoModel(String userUid, String occasionSubtitle, String category, long likes, long dislikes,
                      long reports, int orientation, String url) {
        this.userUid = userUid;
        this.occasionSubtitle = occasionSubtitle;
        this.category = category;
        this.likes = likes;
        this.dislikes = dislikes;
        this.reports = reports;
        this.orientation = orientation;
        this.url = url;
    }

    /**
     * This class returns the userUid id.
     */
    public String getUserUid() {
        return userUid;
    }

    /**
     * This class returns the reason why the photo was taken.
     */
    public String getOccasionSubtitle() {
        return occasionSubtitle;
    }

    /**
     * This class returns the photo category.
     */
    public String getCategory() {
        if(category != null) {
            return category;
        }
        return "Fashion";
    }

    /**
     * This class returns the number of likes the photo has.
     */
    public long getLikes() {
        return likes;
    }

    /**
     * This class returns the number of dislikes the photo has.
     */
    public long getDislikes() {
        return dislikes;
    }

    /**
     * This class returns the number of reports the photo has.
     */
    public long getReports() {
        return reports;
    }

    /**
     * This class returns whether the photo is landscape or portrait.
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * This class returns url of photo in FireBase Storage.
     */
    public String getUrl() {
        return url;
    }

    /**
     * This class sets the userUid property.
     */
    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    /**
     * This class sets the occasionSubtitle property.
     */
    public void setOccasionSubtitle(String occasionSubtitle) {
        this.occasionSubtitle = occasionSubtitle;
    }

    /**
     * This class sets the category property.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * This class sets the likes property.
     */
    public void setLikes(long likes) {
        this.likes = likes;
    }

    /**
     * This class sets the dislikes property.
     */
    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    /**
     * This class sets the reports property.
     */
    public void setReports(long reports) {
        this.reports = reports;
    }

    /**
     * This class sets the orientation property.
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * This class sets the url property.
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
