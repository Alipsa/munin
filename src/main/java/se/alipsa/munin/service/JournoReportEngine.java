package se.alipsa.munin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.journo.JournoException;
import se.alipsa.journo.ReportEngine;
import se.alipsa.munin.model.Report;

import java.util.Map;

@Service
public class JournoReportEngine {

  ReportEngine engine;

  @Autowired
  JournoReportEngine(JournoTemplateLoader journoTemplateLoader) {
    engine = new ReportEngine(journoTemplateLoader);
  }

  public String runJournoReport(Report report, Map<String, Object>... params) throws JournoException {
    if (params.length > 0) {
      return engine.renderHtml(report.getReportName(), params[0]);
    } else {
      return engine.renderHtml(report.getReportName(), null);
    }

  }
}
