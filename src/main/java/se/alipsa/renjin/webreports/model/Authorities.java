package se.alipsa.renjin.webreports.model;

import javax.persistence.*;

@Entity
@Table(name = "authorities")
public class Authorities {

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
