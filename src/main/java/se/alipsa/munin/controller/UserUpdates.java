package se.alipsa.munin.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class for holding a list of UserUpdate objects.
 */
public class UserUpdates {

  private List<UserUpdate> updateList = new ArrayList<>();

  public UserUpdates() {
  }

  /**
   * Constructor with a list of user updates.
   *
   * @param updateList the list of UserUpdate objects
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public UserUpdates(List<UserUpdate> updateList) {
    this.updateList = updateList;
  }

  /**
   * Returns the list of user updates.
   *
   * @return the list of UserUpdate objects
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<UserUpdate> getUpdateList() {
    return updateList;
  }

  /**
   * Adds a UserUpdate to the list.
   *
   * @param userUpdate the UserUpdate to add
   */
  public void addUserUpdate(UserUpdate userUpdate) {
    updateList.add(userUpdate);
  }
}
