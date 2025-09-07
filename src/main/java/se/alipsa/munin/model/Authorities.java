package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entity representing user authorities (roles) in the system.
 * Uses a composite primary key defined in AuthoritiesPk.
 */
@Entity
@Table(name = "authorities")
public class Authorities implements Serializable {

  @EmbeddedId
  private AuthoritiesPk pk;

  /** Default constructor for JPA */
  public Authorities() {
  }

  /** Constructor with composite primary key */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public Authorities(AuthoritiesPk pk) {
    this.pk = pk;
  }

  /** Sets the composite primary key */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setPk(AuthoritiesPk pk) {
    this.pk = pk;
  }

  /** Returns the composite primary key */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public AuthoritiesPk getPk() {
    return pk;
  }

}
