package se.alipsa.munin.service;

/**
 * Custom exception class for file storage-related errors.
 */
public class FileStorageException extends Exception {

  /**
   * Constructs a new FileStorageException with no detail message.
   */
  public FileStorageException(String message) {
    super(message);
  }

  /**
   * Constructs a new FileStorageException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public FileStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
