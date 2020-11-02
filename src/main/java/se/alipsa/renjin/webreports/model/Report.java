package se.alipsa.renjin.webreports.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Report {

  @Id
  @GeneratedValue
  private Long id;
  private String reportName;
  private String description;
  private String reportContent;
  private String inputContent;

  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getReportContent() {
    return reportContent;
  }

  public void setReportContent(String reportContent) {
    this.reportContent = reportContent;
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

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
