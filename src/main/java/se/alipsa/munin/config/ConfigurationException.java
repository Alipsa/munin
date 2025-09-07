package se.alipsa.munin.config;

/**
 * Custom exception class for configuration-related errors.
 */
public class ConfigurationException extends Exception {

  /**
   * Constructs a new ConfigurationException with no detail message.
   */
  public ConfigurationException() {
    super();
  }

  /**
   * Constructs a new ConfigurationException with the specified detail message.
   *
   * @param message the detail message
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * Constructs a new ConfigurationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new ConfigurationException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public ConfigurationException(Throwable cause) {
    super(cause);
  }
}
