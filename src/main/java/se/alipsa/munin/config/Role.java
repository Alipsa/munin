package se.alipsa.munin.config;

/**
 * Enum representing different user roles in the system.
 */
public enum Role {

  /** Role for viewers with limited access */
  ROLE_VIEWER("VIEWER"),
  /** Role for analysts with intermediate access */
  ROLE_ANALYST("ANALYST"),
  /** Role for administrators with full access */
  ROLE_ADMIN("ADMIN");

  // Note that spring boot does some magic on role names and prefixes the, with ROLE_,
  // hence we use the constants in the code but the DB contains the full ROLE_xxx name.
  final String springSecurityName;

  /** Constructor to initialize the role with its Spring Security name */
  Role(String springSecurityName) {
    this.springSecurityName = springSecurityName;
  }

  /** Returns the short name of the role i.e. without the "ROLE_" prefix */
  public String getShortName() {
    return springSecurityName;
  }
}
