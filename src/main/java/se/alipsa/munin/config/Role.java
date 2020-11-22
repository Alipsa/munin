package se.alipsa.munin.config;

public enum Role {

  ROLE_VIEWER("VIEWER"),
  ROLE_ANALYST("ANALYST"),
  ROLE_ADMIN("ADMIN");

  // Note that spring boot does some magic on role names and prefixes the, with ROLE_,
  // hence we use the constants in the code but the DB contains the full ROLE_xxx name.
  String springSecurityName;

  Role(String springSecurityName) {
    this.springSecurityName = springSecurityName;
  }

  public String getShortName() {
    return springSecurityName;
  }
}
