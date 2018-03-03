package tk.talcharnes.unborify.Models;

/**
 * Created by Khuram Chaudhry on 2/26/18.
 * This class is an Object design for the Deals Object.
 *
 */

public class DealsModel {

    private Long id;
    private Integer zipcode;
    private String description;
    private String pictureUrl;

    /**
     * Required empty Constructor for FireBase.
     */
    public DealsModel() {

    }

    /**
     * This method returns the id of the deal in the database.
     */
    public Long getId() {
        return id;
    }

    /**
     * This method returns the location of the deal by zip code.
     */
    public Integer getZipcode() {
        return zipcode;
    }

    /**
     * This method returns the description of the deal.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method returns the url of the photo displayed in the deal.
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * This method sets the id of the deal object.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method sets the location of the deal object.
     */
    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * This method sets the description of the deal object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method sets the photo url of the deal object.
     */
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
