package se.alipsa.munin.controller;

/**
 * A class representing an update to a user's information and roles.
 */
public class UserUpdate {

  private String username;
  private String email;
  private boolean enabled;
  private int failedAttempts;
  private boolean viewer;
  private boolean analyst;
  private boolean admin;

  /**
   * Default constructor for UserUpdate.
   */
  public UserUpdate() {
    // Default constructor
  }

  /**
   * Returns the username of the user.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the email of the user.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email of the user.
   *
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns whether the user is enabled.
   *
   * @return true if the user is enabled, false otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets whether the user is enabled.
   *
   * @param enabled true to enable the user, false to disable
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns the number of failed login attempts.
   *
   * @return the number of failed attempts
   */
  public int getFailedAttempts() {
    return failedAttempts;
  }

  /**
   * Sets the number of failed login attempts.
   *
   * @param failedAttempts the number of failed attempts to set
   */
  public void setFailedAttempts(int failedAttempts) {
    this.failedAttempts = failedAttempts;
  }

  /**
   * Returns whether the user has the viewer role.
   *
   * @return true if the user is a viewer, false otherwise
   */
  public boolean isViewer() {
    return viewer;
  }

  /**
   * Sets whether the user has the viewer role.
   *
   * @param viewer true to assign the viewer role, false to remove it
   */
  public void setViewer(boolean viewer) {
    this.viewer = viewer;
  }

  /**
   * Returns whether the user has the analyst role.
   *
   * @return true if the user is an analyst, false otherwise
   */
  public boolean isAnalyst() {
    return analyst;
  }

  /**
   * Sets whether the user has the analyst role.
   *
   * @param analyst true to assign the analyst role, false to remove it
   */
  public void setAnalyst(boolean analyst) {
    this.analyst = analyst;
  }

  /**
   * Returns whether the user has the admin role.
   *
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets whether the user has the admin role.
   *
   * @param admin true to assign the admin role, false to remove it
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  @Override
  public String toString() {
    return "UserUpdate{" +
        "username='" + username + '\'' +
        ", email=" + email +
        ", enabled=" + enabled +
        ", viewer=" + viewer +
        ", analyst=" + analyst +
        ", admin=" + admin +
        '}';
  }


}
