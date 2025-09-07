package se.alipsa.munin.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for file storage configuration.
 * <p>
 * Properties are configured in application.properties with the prefix "filestorage".
 * </p>
 */
@ConfigurationProperties("filestorage")
public class FileStorageProperties {

  /**
   * Folder location for storing files
   * Overrride in application.properties with filestorage.location=/some/other/path
   */
  private String location = "storage";

  /**
   * Returns the folder location for storing files.
   *
   * @return the folder location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the folder location for storing files.
   *
   * @param location the folder location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

}
