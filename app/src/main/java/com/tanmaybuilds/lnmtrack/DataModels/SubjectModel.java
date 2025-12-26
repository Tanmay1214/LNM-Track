package com.tanmaybuilds.lnmtrack.DataModels;

public class SubjectModel {
    private String subject;
    private String total;
    private String attended;
    private String percentage;

    // Local fields
    private String lastUpdate;
    private String status;

    public SubjectModel(String subject, String total, String attended, String percentage, String lastUpdate, String status) {
        this.subject = subject;
        this.total = total;
        this.attended = attended;
        this.percentage = percentage;
        this.lastUpdate = lastUpdate;
        this.status = status;
    }

    // Getters
    public String getSubject() { return subject; }
    public String getTotal() { return total; }
    public String getAttended() { return attended; }
    public String getPercentage() { return percentage; }
    public String getLastUpdate() { return lastUpdate; }
    public String getStatus() { return status; }

    // Helper method
    public int getPercentageInt() {
        try {
            if (percentage != null) {
                String cleanString = percentage.replace("%", "").trim();
                float value = Float.parseFloat(cleanString);

                return (int) value;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    // Helper method
    public int getMissedCount() {
        try {
            int t = Integer.parseInt(total);
            int a = Integer.parseInt(attended);
            return Math.max(0, t - a);
        } catch (Exception e) {
            return 0;
        }
    }
}