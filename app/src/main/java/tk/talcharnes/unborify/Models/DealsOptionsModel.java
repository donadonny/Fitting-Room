package tk.talcharnes.unborify.Models;

/**
 * Deals options POJO
 */

public class DealsOptionsModel {
    private int radius;
    private String metric;

    public DealsOptionsModel(){

    }

    public DealsOptionsModel(int radius, String metric) {
        this.radius = radius;
        this.metric = metric;

    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "DealsOptions{" +
                "radius=" + radius +
                ", metric='" + metric + '\'' +
                '}';
    }
}