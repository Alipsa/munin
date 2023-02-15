package se.alipsa.munin.service;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.springframework.stereotype.Service;
import se.alipsa.groovy.gmd.Gmd;

import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptContext;
import javax.script.ScriptException;

@Service
public class GroovyReportEngine {

  GroovyScriptEngineImpl engine;

  public GroovyReportEngine() {
    GroovyClassLoader classLoader = new GroovyClassLoader();
    engine = new GroovyScriptEngineImpl(classLoader);
  }

  public String runGroovyReport(String definition, Map<String, Object>... params) throws ScriptException {
    try {
      if (params.length > 0) {
        engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(params[0]);
      }
      return String.valueOf(engine.eval(definition));
    } finally {
      engine.getBindings(ScriptContext.ENGINE_SCOPE).clear();
    }
  }

  public String runGmdReport(String definition, Map<String, Object>... params) {
    var gmd = new Gmd();
    if (params.length == 0) {
      return gmd.gmdToHtml(definition);
    } else {
      return gmd.gmdToHtml(definition, params[0]);
    }
  }
}
