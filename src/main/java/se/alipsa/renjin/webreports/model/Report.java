package se.alipsa.renjin.webreports.model;

import javax.persistence.*;

@Entity
public class Report {

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

  public String getDefinition() {
    return definition;
  }

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

}
