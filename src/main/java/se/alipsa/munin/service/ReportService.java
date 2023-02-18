package se.alipsa.munin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.munin.model.Report;

import java.util.Map;
import javax.script.ScriptException;

@Service
public class ReportService {

  private final RenjinReportEngine renjinReportEngine;
  private final GroovyReportEngine groovyReportEngine;

  @Autowired
  public ReportService(RenjinReportEngine renjinReportEngine, GroovyReportEngine groovyReportEngine) {
    this.renjinReportEngine = renjinReportEngine;
    this.groovyReportEngine = groovyReportEngine;
  }
  @SafeVarargs
  public final String runReport(Report report, Map<String, Object>... params) throws ScriptException, ReportDefinitionException {
    return switch (report.getReportType()) {
      case R, UNMANAGED -> renjinReportEngine.runReport(report.getDefinition(), params);
      case MDR -> renjinReportEngine.runMdrReport(report.getDefinition(), params);
      case GROOVY -> groovyReportEngine.runGroovyReport(report.getDefinition(), params);
      case GMD -> groovyReportEngine.runGmdReport(report.getDefinition(), params);
    };
  }
}
