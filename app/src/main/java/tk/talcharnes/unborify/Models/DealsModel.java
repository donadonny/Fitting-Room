package tk.talcharnes.unborify.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by marzin
 */

@IgnoreExtraProperties
public class DealsModel {
    private String name;
    private String website;
    private double price;
    private String expirationDate;
    private String details;

    public DealsModel(){}

    public DealsModel(String name, String website, double price, String expirationDate, String details) {
        this.name = name;
        this.website = website;
        this.price = price;
        this.expirationDate = expirationDate;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}