package se.alipsa.munin.model;

public enum ReportType {
  GROOVY, GMD, JOURNO,
  @Deprecated R,
  @Deprecated UNMANAGED;


  public static ReportType[] activeValues() {
    return new ReportType[]{GROOVY, GMD, JOURNO};
  }
}
