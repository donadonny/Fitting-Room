package tk.talcharnes.unborify;

import java.util.HashMap;

/**
 * Created by khuramchaudhry on 8/26/17.
 */

public class Report {

    private int numReports;
    private HashMap<String, String>  reported_by;

    public Report() {

    }

    public Report(int numReports, HashMap<String, String> reported_by) {
        this.numReports = numReports;
        this.reported_by = reported_by;
    }

    public void setNumReports(int numReports) {
        this.numReports = numReports;
    }

    public void setReports(HashMap<String, String> reported_by) {
        this.reported_by = reported_by;
    }

    public int getNumReports() {
        return this.numReports;
    }

    public HashMap<String, String> getReported_by() {
        return this.reported_by;
    }
}
