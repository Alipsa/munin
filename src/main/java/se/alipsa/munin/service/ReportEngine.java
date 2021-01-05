package se.alipsa.munin.service;

import org.renjin.eval.EvalException;
import org.renjin.parser.ParseException;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.renjin.starter.RenjinSessionEnginePool;

import javax.script.ScriptException;
import java.util.Map;

@Service
public class ReportEngine {

  private final RenjinSessionEnginePool renjinSessionEnginePool;

  private static final Logger LOG = LoggerFactory.getLogger(ReportEngine.class);

  @Autowired
  public ReportEngine(RenjinSessionEnginePool renjinSessionEnginePool) {
    this.renjinSessionEnginePool = renjinSessionEnginePool;
  }

  @SafeVarargs
  public final String runReport(String script, Map<String, Object>... params) throws ScriptException, ReportDefinitionException {
    SEXP sexp = runScript(script, params);
    if (!(sexp instanceof StringArrayVector)) {
      throw new ReportDefinitionException("This report does not return an html String (character), nothing to render!");
    }
    return sexp.asString();
  }

  @SafeVarargs
  private final SEXP runScript(String script, Map<String, Object>... paramOpt) throws ScriptException {
    RenjinScriptEngine scriptEngine = null;
    try {
      scriptEngine = renjinSessionEnginePool.borrowObject();
      if (paramOpt.length > 0) {
        Map<String, Object> params = paramOpt[0];
        params.forEach(scriptEngine::put);
      }
      return (SEXP) scriptEngine.eval(script);
    } catch (Exception e) {
      if (e instanceof ScriptException) {
        throw (ScriptException) e;
      } else if (e instanceof EvalException) {
        LOG.warn("A runtime evaluation error occurred when running the R script", e);
        throw (EvalException)e;
      } else if(e instanceof ParseException) {
        LOG.warn("Script is not parsable R code", e);
        throw (ParseException)e;
      } else {
        LOG.error("Error using object pool", e);
        throw new ReportEngineRuntimeException("Error using object pool", e);
      }
    } finally {
      if (scriptEngine != null) {
        renjinSessionEnginePool.returnObject(scriptEngine);
      }
    }
  }

  @SafeVarargs
  public final String runMdrReport(String script, Map<String, Object>... paramOpt) throws ScriptException {
    RenjinScriptEngine scriptEngine = null;
    try {
      scriptEngine = renjinSessionEnginePool.borrowObject();
      if (paramOpt.length > 0) {
        Map<String, Object> params = paramOpt[0];
        params.forEach(scriptEngine::put);
      }
      scriptEngine.put("mdrContent", script);

      return ((SEXP) scriptEngine.eval("library('se.alipsa:mdr2html')\n parseMdr(mdrContent)")).asString();
    } catch (Exception e) {
      if (e instanceof ScriptException) {
        throw (ScriptException) e;
      } else if (e instanceof EvalException) {
        LOG.warn("A runtime evaluation error occurred when running the mdr report", e);
        throw (EvalException) e;
      } else if(e instanceof ParseException) {
        LOG.warn("Mdr document contains non parsable R code", e);
        throw (ParseException)e;
      } else {
        LOG.error("Error using object pool", e);
        throw new ReportEngineRuntimeException("Error using object pool", e);
      }
    } finally {
      if (scriptEngine != null) {
        renjinSessionEnginePool.returnObject(scriptEngine);
      }
    }
  }
}
