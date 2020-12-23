package se.alipsa.munin.model.web;

import java.util.Objects;

public class ReportScheduleWeb implements Comparable<ReportScheduleWeb> {

  private Long id;
  private String reportName;
  private String cron;
  private String emails;
  private String readableCron;

  public ReportScheduleWeb() {
    // empty
  }

  public ReportScheduleWeb(Long id, String reportName, String cron, String emails, String readableCron) {
    this.id = id;
    this.reportName = reportName;
    this.cron = cron;
    this.emails = emails;
    this.readableCron = readableCron;

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Override
  public int compareTo(ReportScheduleWeb other) {
    return (reportName + cron).compareTo(other.getReportName() + other.getCron());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    ReportScheduleWeb that = (ReportScheduleWeb) obj;
    return Objects.equals(id, that.id) && Objects.equals(reportName, that.reportName) && Objects.equals(cron, that.cron) && Objects.equals(emails, that.emails) && Objects.equals(readableCron, that.readableCron);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, reportName, cron, emails, readableCron);
  }
}
