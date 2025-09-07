package se.alipsa.munin.model.web;

import java.util.Objects;

/**
 * A web representation of a report schedule, including details such as report name, cron expression, and email recipients.
 */
public class ReportScheduleWeb implements Comparable<ReportScheduleWeb> {

  private Long id;
  private String reportName;
  private String cron;
  private String emails;
  private String readableCron;

  /**
   * Default constructor for ReportScheduleWeb.
   */
  public ReportScheduleWeb() {
    // empty
  }

  /**
   * Constructs a ReportScheduleWeb with the specified details.
   *
   * @param id            the unique identifier of the report schedule
   * @param reportName    the name of the report
   * @param cron          the cron expression for scheduling
   * @param emails        the email recipients for the report
   * @param readableCron  a human-readable description of the cron expression
   */
  public ReportScheduleWeb(Long id, String reportName, String cron, String emails, String readableCron) {
    this.id = id;
    this.reportName = reportName;
    this.cron = cron;
    this.emails = emails;
    this.readableCron = readableCron;

  }

  /**
   * Returns the unique identifier of the report schedule.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the report schedule.
   *
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the name of the report.
   *
   * @return the reportName
   */
  public String getReportName() {
    return reportName;
  }

  /**
   * Sets the name of the report.
   *
   * @param reportName the reportName to set
   */
  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  /**
   * Returns the cron expression for scheduling the report.
   *
   * @return the cron
   */
  public String getCron() {
    return cron;
  }

  /**
   * Sets the cron expression for scheduling the report.
   *
   * @param cron the cron to set
   */
  public void setCron(String cron) {
    this.cron = cron;
  }

  /**
   * Returns the email recipients for the report.
   *
   * @return the emails
   */
  public String getEmails() {
    return emails;
  }

  /**
   * Sets the email recipients for the report.
   *
   * @param emails the emails to set
   */
  public void setEmails(String emails) {
    this.emails = emails;
  }

  /**
   * Returns a human-readable description of the cron expression.
   *
   * @return the readableCron
   */
  public String getReadableCron() {
    return readableCron;
  }

  /**
   * Sets a human-readable description of the cron expression.
   *
   * @param readableCron the readableCron to set
   */
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
