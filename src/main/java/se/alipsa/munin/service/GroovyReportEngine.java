package se.alipsa.munin.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.springframework.stereotype.Service;
import se.alipsa.groovy.gmd.Gmd;
import se.alipsa.groovy.gmd.GmdException;
import se.alipsa.munin.model.Report;

import java.util.Map;
import javax.script.ScriptContext;
import javax.script.ScriptException;

@Service
public class GroovyReportEngine {

  GroovyScriptEngineImpl engine;
  Gmd gmd;

  // spotbugs wants us to do this in a doPrivileged block but SecurityManager is deprecated, so we suppress the warning
  @SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
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
