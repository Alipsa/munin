package se.alipsa.munin.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("filestorage")
public class FileStorageProperties {

  /**
   * Folder location for storing files
   * Overrride in application.properties with filestorage.location=/some/other/path
   */
  private String location = "storage";

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

}
