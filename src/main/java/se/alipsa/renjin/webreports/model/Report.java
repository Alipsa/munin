package se.alipsa.renjin.webreports.model;

public class Report {

  private String reportName;
  private String reportContent;
  private String inputContent;

  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
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
}
