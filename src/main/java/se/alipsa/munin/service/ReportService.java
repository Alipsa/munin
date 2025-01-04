package se.alipsa.munin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.groovy.gmd.GmdException;
import se.alipsa.journo.JournoException;
import se.alipsa.munin.model.Report;

import java.util.Map;
import javax.script.ScriptException;

@Service
public class ReportService {

  private final GroovyReportEngine groovyReportEngine;
  private final JournoReportEngine journoReportEngine;

  @Autowired
  public ReportService(GroovyReportEngine groovyReportEngine, JournoReportEngine journoReportEngine) {
    this.groovyReportEngine = groovyReportEngine;
    this.journoReportEngine = journoReportEngine;
  }
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
