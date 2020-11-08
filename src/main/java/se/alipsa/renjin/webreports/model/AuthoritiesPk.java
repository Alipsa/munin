package se.alipsa.renjin.webreports.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

  public AuthoritiesPk(User user, String authority) {
    this.user = user;
    this.authority = authority;
  }

  public User getUser() {
    return user;
  }

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
