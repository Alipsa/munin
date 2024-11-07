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

  private final RenjinReportEngine renjinReportEngine;
  private final GroovyReportEngine groovyReportEngine;
  private final JournoReportEngine journoReportEngine;

  @Autowired
  public ReportService(RenjinReportEngine renjinReportEngine, GroovyReportEngine groovyReportEngine, JournoReportEngine journoReportEngine) {
    this.renjinReportEngine = renjinReportEngine;
    this.groovyReportEngine = groovyReportEngine;
    this.journoReportEngine = journoReportEngine;
  }
  @SafeVarargs
  public final String runReport(Report report, Map<String, Object>... params) throws ScriptException, ReportDefinitionException, GmdException, JournoException {
    return switch (report.getReportType()) {
      case R, UNMANAGED -> renjinReportEngine.runReport(report, params);
      case MDR -> renjinReportEngine.runMdrReport(report, params);
      case GROOVY -> groovyReportEngine.runGroovyReport(report, params);
      case GMD -> groovyReportEngine.runGmdReport(report, params);
      case JOURNO -> journoReportEngine.runJournoReport(report, params);
    };
  }
}
