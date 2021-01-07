package se.alipsa.munin.model.web;

public class ReportGroupInfo {

  private String reportGroup;
  private Long count;

  public ReportGroupInfo(String groupName, Long count) {
    this.reportGroup = groupName;
    this.count = count;
  }
  public String getReportGroup() {
    return reportGroup;
  }

  public void setReportGroup(String reportGroup) {
    this.reportGroup = reportGroup;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
