package se.alipsa.munin.service;

import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.journo.JournoEngine;
import se.alipsa.journo.JournoException;
import se.alipsa.munin.model.Report;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for running reports written in Journo with optional Groovy preprocessing.
 */
@Service
public class JournoReportEngine {

  private static Logger LOG = LoggerFactory.getLogger(JournoReportEngine.class);
  JournoEngine journoEngine;
  GroovyScriptEngineImpl groovyEngine;

  /**
   * Constructor with autowired dependencies.
   *
   * @param journoTemplateLoader the Journo template loader
   */
  @Autowired
  JournoReportEngine(JournoTemplateLoader journoTemplateLoader) {
    journoEngine = new JournoEngine(journoTemplateLoader);
    groovyEngine = new GroovyScriptEngineImpl();
  }

  /**
   * Run a Journo report and return the html result.
   *
   * @param report the report to run
   * @param params a Map of String and Object with the parameters for the report (optional)
   * @return the html result
   * @throws ScriptException if groovy code failed
   * @throws JournoException if some other part of running the report failed
   */
  public String runJournoReport(Report report, Map<String, Object>... params) throws ScriptException, JournoException {
    Map<String, Object> p = runPreprocessingCode(report, params);
    //LOG.info("Journo report: {}", report);
    return journoEngine.renderHtml(report.getReportName(), p);
  }

  private Map<String, Object> runPreprocessingCode(Report report, Map<String, Object>... params) throws ScriptException {
    try {
      if (params.length > 0) {
        groovyEngine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(params[0]);
      }
      String ppCode = report.getPreProcessing();
      if (ppCode == null || ppCode.isBlank() ) {
        return params.length > 0 ? params[0] : new HashMap<>();
      }
      Object result = groovyEngine.eval(ppCode);
      if (result instanceof Map) {
        return (Map<String, Object>) result;
      } else {
        var type = result == null ? "null" : result.getClass().getName();
        LOG.warn("Unexpected return type {}, was expecting a Map<String, Object>", type);
        throw new ScriptException("The preprocessing script does not return a Map but a " + type + ", cannot continue");
      }
    } finally {
      groovyEngine.getBindings(ScriptContext.ENGINE_SCOPE).clear();
    }
  }
}
