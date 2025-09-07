package se.alipsa.munin.model;

/**
 * Enum representing different types of reports.
 */
public enum ReportType {
  /** Report generated using Groovy scripts */
  GROOVY,
  /** Report generated using GMD (Groovy Markdown) */
  GMD,
  /** Report generated using Journo */
  JOURNO,
  /** Deprecated report type R */
  @Deprecated R,
  /** Deprecated report type UNMANAGED */
  @Deprecated UNMANAGED;

  /**
   * Returns an array of active report types, excluding deprecated ones.
   *
   * @return an array of active ReportType values
   */
  public static ReportType[] activeValues() {
    return new ReportType[]{GROOVY, GMD, JOURNO};
  }
}
