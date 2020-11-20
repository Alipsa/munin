package se.alipsa.renjin.webreports.service;

import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.Map;

@Service
public class ReportEngine {

  private final RenjinScriptEngine scriptEngine;

  @Autowired
  public ReportEngine(RenjinScriptEngine scriptEngine) {
    this.scriptEngine = scriptEngine;
  }

  public String runReport(String script) throws ScriptException, ReportDefinitionException {
    SEXP sexp = runScript(script);
    if (!(sexp instanceof StringArrayVector)) {
      throw new ReportDefinitionException("This report does not return an html String (character), nothing to render!");
    }
    return sexp.asString();
  }

  public String runReport(String script, Map<String, Object> params) throws ScriptException, ReportDefinitionException {
    SEXP sexp = runScript(script, params);
    if (!(sexp instanceof StringArrayVector)) {
      throw new ReportDefinitionException("This report does not return an html String (character), nothing to render!");
    }
    return sexp.asString();
  }

  private SEXP runScript(String script) throws ScriptException {
    SEXP result = (SEXP)scriptEngine.eval(script);
    clearEnvironment();
    return result;
  }

  private SEXP runScript(String script, Map<String, Object> params) throws ScriptException {
    params.forEach(scriptEngine::put);
    SEXP result = (SEXP)scriptEngine.eval(script);
    clearEnvironment();
    return result;
  }


  /**
   * will clear all objects includes hidden objects in the environment.
   */
  public void clearEnvironment() {
    try {
      scriptEngine.eval("rm(list = ls(all.names = TRUE))");
    } catch (ScriptException e) {
      throw new RuntimeException("Failed to clean environment", e);
    }
  }
}
