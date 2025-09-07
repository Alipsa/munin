package se.alipsa.munin.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a report in the system.
 * Contains details such as name, description, type, group, and content.
 */
@Entity
public class Report implements Serializable {

  /**
   * Default constructor for JPA.
   */
  public Report() {
    // default constructor for JPA
  }

  @Id
  @Column(length = 50)
  private String reportName;

  @Column(length = 500)
  private String description;

  @Lob
  @Column(length = 15000)
  private String preProcessing;

  @Lob
  @Column(length = 15000)
  private String template;

  @Lob
  @Column(length = 9000)
  private String inputContent;

  @Enumerated(EnumType.STRING)
  ReportType reportType;

  @Column(length = 50)
  private String reportGroup;

  @CreationTimestamp
  @Column
  private LocalDateTime insertedAt;

  @UpdateTimestamp
  @Column
  private LocalDateTime updatedAt;

  /**
   * @return the name of the report
   */
  public String getReportName() {
    return reportName;
  }

  /**
   * Set the name of the report
   * @param reportName, the name of the report
   */
  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  /**
   *
   * @return a short description of the report
   */
  public String getDescription() {
    return description;
  }

  /**
   *
   * @param description, a short description of the report (e.g. what it does)
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the input html content used as parameters for the report
   */
  public String getInputContent() {
    return inputContent;
  }

  /**
   *
   * @param inputContent the input html content used as parameters for the report
   */
  public void setInputContent(String inputContent) {
    this.inputContent = inputContent;
  }

  /**
   * Preprocessing code, it takes the parameters as input and serves the template
   * with the data required to run the report
   * @return the preProcessing code
   */
  public String getPreProcessing() {
    return preProcessing;
  }

  /**
   *
   * @param preProcessing Preprocessing code, it takes the parameters as input and serves the template
   * with the data required to run the report
   */
  public void setPreProcessing(String preProcessing) {
    this.preProcessing = preProcessing;
  }

  /**
   *
   * @return the reporting code logic (the code to execute)
   */
  public String getTemplate() {
    return template;
  }

  /**
   *
   * @param reportContent, the reporting code logic (the code to execute)
   */
  public void setTemplate(String reportContent) {
    this.template = reportContent;
  }

  /**
   * @return the type of report, e.g. Groovy, GMD or Journo
   */
  public ReportType getReportType() {
    return reportType;
  }

  /**
   * Set the type of report, e.g. Groovy, GMD or Journo
   * @param reportType the type of report
   */
  public void setReportType(ReportType reportType) {
    this.reportType = reportType;
  }

  /**
   * @return the report group, used to group reports in the UI
   */
  public String getReportGroup() {
    return reportGroup;
  }

  /**
   * Set the report group, used to group reports in the UI
   * @param reportGroup the report group
   */
  public void setReportGroup(String reportGroup) {
    this.reportGroup = reportGroup;
  }

  /**
   * The time the report was inserted into the database.
   *
   * @return the time the report was inserted
   */
  public LocalDateTime getInsertedAt() {
    return insertedAt;
  }

  /**
   * Set the time the report was inserted into the database.
   *
   * @param insertedAt the time the report was inserted
   */
  public void setInsertedAt(LocalDateTime insertedAt) {
    this.insertedAt = insertedAt;
  }

  /**
   * The last time the report was updated.
   *
   * @return the last time the report was updated
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Set the last time the report was updated.
   *
   * @param updatedAt the last time the report was updated
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Report{" +
           "reportName='" + reportName + '\'' +
           ", description='" + description + '\'' +
           ", reportType=" + reportType +
           ", reportGroup='" + reportGroup + '\'' +
           ", isParameterized=" + (inputContent == null ? "No" : inputContent.length() > 0 ? "Yes" : "No") +
           '}';
  }
}
