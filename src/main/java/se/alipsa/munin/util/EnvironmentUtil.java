package se.alipsa.munin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class EnvironmentUtil {

  Environment environment;

  private String port;
  private String hostname;

  @Autowired
  public EnvironmentUtil(Environment environment) {
    this.environment = environment;
  }

  public String getPort() {
    if (port == null) port = environment.getProperty("local.server.port");
    return port;
  }

  public Integer getPortAsInt() {
    return Integer.valueOf(getPort());
  }

  public String getHostname() {
    try {
      if (hostname == null) hostname = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      hostname = "localhost";
    }
    return hostname;
  }

  public String getBaseUrl() {
    return "http://" + getHostname() + ":" + getPort();
  }
}
