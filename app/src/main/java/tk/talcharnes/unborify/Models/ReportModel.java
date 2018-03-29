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
 *      reportedBy - holds information of who is reporting as the key and the value as the time it
 *          was report on.
 */

public class ReportModel {

    public final static String NUM_REPORTS_KEY = "numReports";
    public final static String REPORTED_BY_KEY = "reportedBy";

    private int numReports;
    private HashMap<String, Long> reportedBy;

    /**
     * Required empty Constructor for FireBase.
     */
    public ReportModel() {

    }

    /**
     * Custom Constructor.
     */
    public ReportModel(int numReports, HashMap<String, Long> reportedBy) {
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
    public HashMap<String, Long> getReportedBy() {
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
    public void setReportedBy(HashMap<String, Long> reportedBy) {
        this.reportedBy = reportedBy;
    }

}
