package se.alipsa.munin.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "authorities")
public class Authorities implements Serializable {

  @EmbeddedId
  private AuthoritiesPk pk;

  public Authorities() {
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public Authorities(AuthoritiesPk pk) {
    this.pk = pk;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setPk(AuthoritiesPk pk) {
    this.pk = pk;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public AuthoritiesPk getPk() {
    return pk;
  }

}
