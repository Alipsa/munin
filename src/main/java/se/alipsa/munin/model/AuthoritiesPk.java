package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthoritiesPk implements Serializable {

  @ManyToOne
  @JoinColumn(name = "username")
  private User user;

  @Column(length = 50)
  private String authority;

  public AuthoritiesPk() {
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AuthoritiesPk(User user, String authority) {
    this.user = user;
    this.authority = authority;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public User getUser() {
    return user;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public void setUser(User user) {
    this.user = user;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthoritiesPk that = (AuthoritiesPk) o;
    return user.equals(that.user) &&
        authority.equals(that.authority);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, authority);
  }
}
