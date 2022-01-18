package se.alipsa.munin.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.renjin.eval.EvalException;
import org.renjin.parser.ParseException;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.munin.util.EnvironmentUtil;
import se.alipsa.renjin.starter.RenjinSessionEnginePool;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.Map;

@Service
public class ReportEngine {


  private final RenjinSessionEnginePool renjinSessionEnginePool;
  private final EnvironmentUtil environmentUtil;

  private static final Logger LOG = LoggerFactory.getLogger(ReportEngine.class);

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public ReportEngine(RenjinSessionEnginePool renjinSessionEnginePool, EnvironmentUtil environmentUtil) {
    this.renjinSessionEnginePool = renjinSessionEnginePool;
    this.environmentUtil = environmentUtil;
  }

  @SafeVarargs
  public final String runReport(String script, Map<String, Object>... params) throws ScriptException, ReportDefinitionException {
    SEXP sexp = runScript(script, params);
    if (!(sexp instanceof StringArrayVector)) {
      sexp = runScript("as.character(.runScriptResult)", Collections.singletonMap(".runScriptResult", sexp));
      LOG.info("Script did not return a character vector, so converted it with as.character()");
      //throw new ReportDefinitionException("This report does not return an html String (character), nothing to render!");
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
      scriptEngine.put("muninBaseUrl", environmentUtil.getBaseUrl());
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
      scriptEngine.put("muninBaseUrl", environmentUtil.getBaseUrl());
      scriptEngine.put("mdrContent", script);

      return ((SEXP) scriptEngine.eval("library('se.alipsa:mdr')\n renderMdr(mdrContent)")).asString();
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
