package se.alipsa.munin.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "authorities")
public class Authorities implements Serializable {

  @EmbeddedId
  private AuthoritiesPk pk;

  public Authorities() {
  }

  public Authorities(AuthoritiesPk pk) {
    this.pk = pk;
  }

  public void setPk(AuthoritiesPk pk) {
    this.pk = pk;
  }

  public AuthoritiesPk getPk() {
    return pk;
  }

}
