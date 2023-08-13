package se.alipsa.munin.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Report implements Serializable {

  @Id
  @Column(length = 50)
  private String reportName;

  @Column(length = 500)
  private String description;

  @Lob
  @Column(length = 15000)
  private String definition;

  @Lob
  @Column(length = 9000)
  private String inputContent;

  @Enumerated(EnumType.STRING)
  ReportType reportType;

  @Column(length = 50)
  private String reportGroup;

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
   *
   * @return the reporting code logic (the code to execute)
   */
  public String getDefinition() {
    return definition;
  }

  /**
   *
   * @param reportContent, the reporting code logic (the code to execute)
   */
  public void setDefinition(String reportContent) {
    this.definition = reportContent;
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
