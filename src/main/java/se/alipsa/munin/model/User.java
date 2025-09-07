package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a user in the system.
 * Contains user details such as username, password, email, enabled status, and failed login attempts.
 * Also maintains a list of authorities (roles) associated with the user.
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

  @Id
  @Column(length = 50)
  private String username;
  @Column(length = 60)
  private String password;
  @Column(length = 50)
  private String email;
  private boolean enabled;
  private int failedAttempts;

  /**
   * Default constructor for JPA.
   */
  public User() {
    // default constructor for JPA
  }

  @OneToMany(mappedBy = "pk.user")
  List<Authorities> authorities;

  /**
   * Gets the username of the user.
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
   * Gets the password of the user.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the user.
   *
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Checks if the user is enabled.
   *
   * @return true if the user is enabled, false otherwise
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled status of the user.
   *
   * @param enabled true to enable the user, false to disable
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Gets the number of failed login attempts for the user.
   *
   * @return the number of failed attempts
   */
  public int getFailedAttempts() {
    return failedAttempts;
  }

  /**
   * Sets the number of failed login attempts for the user.
   * @param failedAttempts the number of failed attempts to set
   */
  public void setFailedAttempts(int failedAttempts) {
    this.failedAttempts = failedAttempts;
  }

  /**
   * Gets the email address of the user.
   * @return the user's email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the user.
   * @param email the email address to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the list of authorities (roles) associated with the user.
   * @return the list of authorities
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<Authorities> getAuthorities() {
    return authorities;
  }

  /**
   * Convenience method to check if the user has the "ROLE_VIEWER" authority.
   *
   * @return true if the user has the "ROLE_VIEWER" authority, false otherwise
   */
  public boolean isViewer() {
    Authorities a = getAuthorities().stream()
        .filter(p -> "ROLE_VIEWER".equals(p.getPk().getAuthority()))
        .findAny().orElse(null);
    return a != null;
  }

  /**
   * Convenience method to check if the user has the "ROLE_ANALYST" authority.
   *
   * @return true if the user has the "ROLE_ANALYST" authority, false otherwise
   */
  public boolean isAnalyst() {
    Authorities a = getAuthorities().stream()
        .filter(p -> "ROLE_ANALYST".equals(p.getPk().getAuthority()))
        .findAny().orElse(null);
    return a != null;
  }

  /**
   * Convenience method to check if the user has the "ROLE_ADMIN" authority.
   *
   * @return true if the user has the "ROLE_ADMIN" authority, false otherwise
   */
  public boolean isAdmin() {
    Authorities a = getAuthorities().stream()
        .filter(p -> "ROLE_ADMIN".equals(p.getPk().getAuthority()))
        .findAny().orElse(null);
    return a != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return username.equals(user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
