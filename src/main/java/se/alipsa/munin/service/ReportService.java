package se.alipsa.munin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.gmd.core.GmdException;
import se.alipsa.journo.JournoException;
import se.alipsa.munin.model.Report;

import java.util.Map;
import javax.script.ScriptException;

/**
 * Service for running reports of various types.
 */
@Service
public class ReportService {

  private final GroovyReportEngine groovyReportEngine;
  private final JournoReportEngine journoReportEngine;

  /**
   * Constructor with autowired dependencies.
   *
   * @param groovyReportEngine the Groovy report engine
   * @param journoReportEngine the Journo report engine
   */
  @Autowired
  public ReportService(GroovyReportEngine groovyReportEngine, JournoReportEngine journoReportEngine) {
    this.groovyReportEngine = groovyReportEngine;
    this.journoReportEngine = journoReportEngine;
  }

  /**
   * Run a report and return the html result.
   *
   * @param report the report to run
   * @param params a Map of String and Object with the parameters for the report (optional)
   * @return the html result
   * @throws ScriptException if groovy code failed
   * @throws GmdException if some other part of running a GMD report failed
   * @throws JournoException if some other part of running a Journo report failed
   */
  @SafeVarargs
  public final String runReport(Report report, Map<String, Object>... params) throws ScriptException, GmdException, JournoException {
    return switch (report.getReportType()) {
      case GROOVY, UNMANAGED -> groovyReportEngine.runGroovyReport(report, params);
      case GMD -> groovyReportEngine.runGmdReport(report, params);
      case JOURNO -> journoReportEngine.runJournoReport(report, params);
      case R -> throw new UnsupportedOperationException("R is no longer a supported report type: ");
    };
  }
}
