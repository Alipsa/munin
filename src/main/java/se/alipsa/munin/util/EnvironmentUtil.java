package se.alipsa.munin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility class for accessing environment properties such as server port and hostname.
 */
@Component
public class EnvironmentUtil {

  Environment environment;

  private String port;
  private String hostname;

  /**
   * Constructor with autowired dependencies.
   *
   * @param environment the Spring Environment to access properties
   */
  @Autowired
  public EnvironmentUtil(Environment environment) {
    this.environment = environment;
  }

  /**
   * Gets the server port from the environment properties.
   *
   * @return the server port as a String
   */
  public String getPort() {
    if (port == null) port = environment.getProperty("local.server.port");
    return port;
  }

  /**
   * Gets the server port as an Integer.
   *
   * @return the server port as an Integer
   */
  public Integer getPortAsInt() {
    return Integer.valueOf(getPort());
  }

  /**
   * Gets the hostname of the server. If it cannot be determined, defaults to "localhost".
   *
   * @return the hostname as a String
   */
  public String getHostname() {
    try {
      if (hostname == null) hostname = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      hostname = "localhost";
    }
    return hostname;
  }

  /**
   * Constructs the base URL of the server using the hostname and port.
   *
   * @return the base URL as a String
   */
  public String getBaseUrl() {
    return "http://" + getHostname() + ":" + getPort();
  }
}
