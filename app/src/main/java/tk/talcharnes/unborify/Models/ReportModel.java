package tk.talcharnes.unborify.Models;

import java.util.HashMap;

/**
 * Created by Khuram Chaudhry on 8/26/17.
 *
 * This is an object representation of the reporting used in the database. This object is used for
 *      reporting comments, photos, and among other things.
 *
 * Properties:
 *      numReports - holds the number of reports, used for easy access.
 *      reportedBy - holds information of who is reporting as the key and the value as the reason
 *                      which is optional.
 */

public class ReportModel {

    private int numReports;
    private HashMap<String, String> reportedBy;

    /**
     * Required empty Constructor for FireBase.
     */
    public ReportModel() {

    }

    /**
     * Default Constructor.
     */
    public ReportModel(int numReports, HashMap<String, String> reportedBy) {
        this.numReports = numReports;
        this.reportedBy = reportedBy;
    }

    /**
     * This class returns number of reports.
     */
    public int getNumReports() {
        return this.numReports;
    }

    /**
     * This class return a dictionary of the user who made the report.
     */
    public HashMap<String, String> getReportedBy() {
        return this.reportedBy;
    }

    /**
     * This class sets the number of reports.
     */
    public void setNumReports(int numReports) {
        this.numReports = numReports;
    }

    /**
     * This class sets the reportedBy property.
     */
    public void setReportedBy(HashMap<String, String> reportedBy) {
        this.reportedBy = reportedBy;
    }

}
