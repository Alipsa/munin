package se.alipsa.renjin.webreports.controller;

public class UserUpdate {

  private String username;
  private String email;
  private boolean enabled;
  private int failedAttempts;
  private boolean viewer;
  private boolean analyst;
  private boolean admin;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getFailedAttempts() {
    return failedAttempts;
  }

  public void setFailedAttempts(int failedAttempts) {
    this.failedAttempts = failedAttempts;
  }

  public boolean isViewer() {
    return viewer;
  }

  public void setViewer(boolean viewer) {
    this.viewer = viewer;
  }

  public boolean isAnalyst() {
    return analyst;
  }

  public void setAnalyst(boolean analyst) {
    this.analyst = analyst;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  @Override
  public String toString() {
    return "UserUpdate{" +
        "username='" + username + '\'' +
        ", enabled=" + enabled +
        ", viewer=" + viewer +
        ", analyst=" + analyst +
        ", admin=" + admin +
        '}';
  }


}
