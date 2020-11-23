package se.alipsa.munin.model.web;

public class ReportScheduleWeb {

  private String reportName;
  private String cron;
  private String emails;
  private String readableCron;

  public ReportScheduleWeb() {
    // empty
  }
  public ReportScheduleWeb(String reportName, String cron, String emails, String readableCron) {
    this.reportName = reportName;
    this.cron = cron;
    this.emails = emails;
    this.readableCron = readableCron;

  }

  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public String getEmails() {
    return emails;
  }

  public void setEmails(String emails) {
    this.emails = emails;
  }

  public String getReadableCron() {
    return readableCron;
  }

  public void setReadableCron(String readableCron) {
    this.readableCron = readableCron;
  }
}
