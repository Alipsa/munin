package se.alipsa.munin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReportSchedule {

  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="schedule_seq")
  @SequenceGenerator(name="schedule_seq", sequenceName="schedule_seq", allocationSize=1)
  private Long id;

  private String reportName;
  private String cron;
  private String emails;

  public ReportSchedule() {
    // Default
  }

  public ReportSchedule(String reportName, String cron, String emails) {
    this.reportName = reportName;
    this.cron = cron;
    this.emails = emails;
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
}
