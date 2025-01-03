package se.alipsa.munin.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Report implements Serializable {

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

  public ReportType getReportType() {
    return reportType;
  }

  public void setReportType(ReportType reportType) {
    this.reportType = reportType;
  }

  public String getReportGroup() {
    return reportGroup;
  }

  public void setReportGroup(String reportGroup) {
    this.reportGroup = reportGroup;
  }

  public LocalDateTime getInsertedAt() {
    return insertedAt;
  }

  public void setInsertedAt(LocalDateTime insertedAt) {
    this.insertedAt = insertedAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

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
