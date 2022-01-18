package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

  @OneToMany(mappedBy = "pk.user")
  List<Authorities> authorities;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<Authorities> getAuthorities() {
    return authorities;
  }

  public boolean isViewer() {
    Authorities a = getAuthorities().stream()
        .filter(p -> "ROLE_VIEWER".equals(p.getPk().getAuthority()))
        .findAny().orElse(null);
    return a != null;
  }

  public boolean isAnalyst() {
    Authorities a = getAuthorities().stream()
        .filter(p -> "ROLE_ANALYST".equals(p.getPk().getAuthority()))
        .findAny().orElse(null);
    return a != null;
  }

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
