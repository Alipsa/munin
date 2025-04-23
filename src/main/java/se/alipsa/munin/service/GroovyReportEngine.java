package se.alipsa.munin.service;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.springframework.stereotype.Service;
import se.alipsa.gmd.core.Gmd;
import se.alipsa.gmd.core.GmdException;
import se.alipsa.munin.model.Report;

import java.util.Map;
import javax.script.ScriptContext;
import javax.script.ScriptException;

@Service
public class GroovyReportEngine {

  GroovyScriptEngineImpl engine;
  Gmd gmd;

  public GroovyReportEngine() {
    var classLoader = new GroovyClassLoader();
    engine = new GroovyScriptEngineImpl(classLoader);
    gmd = new Gmd();
  }

  @SafeVarargs
  public final String runGroovyReport(Report report, Map<String, Object>... params) throws ScriptException {
    try {
      if (params.length > 0) {
        engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(params[0]);
      }
      return String.valueOf(engine.eval(report.getTemplate()));
    } finally {
      engine.getBindings(ScriptContext.ENGINE_SCOPE).clear();
    }
  }

  @SafeVarargs
  public final String runGmdReport(Report report, Map<String, Object>... params) throws GmdException {
    if (params.length == 0) {
      return gmd.gmdToHtml(report.getTemplate());
    } else {
      return gmd.gmdToHtml(report.getTemplate(), params[0]);
    }
  }
}
