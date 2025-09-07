package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for Authorities entity.
 * Consists of a User and an authority string.
 */
@Embeddable
public class AuthoritiesPk implements Serializable {

  @ManyToOne
  @JoinColumn(name = "username")
  private User user;

  @Column(length = 50)
  private String authority;

  /** Default constructor for JPA */
  public AuthoritiesPk() {
  }

  /**
   * Constructs an AuthoritiesPk with the specified user and authority.
   * @param user the user associated with this authority
   * @param authority the authority string (e.g., role name)
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public AuthoritiesPk(User user, String authority) {
    this.user = user;
    this.authority = authority;
  }

  /**
   * Gets the user for this authority
   * @return the user
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public User getUser() {
    return user;
  }

  /**
   * Sets the user for this authority
   * @param user the user to set
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public void setUser(User user) {
    this.user = user;
  }

  public String getAuthority() {
    return authority;
  }

  /**
   * Sets the authority string (e.g., role name)
   * @param authority the authority to set
   */
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
