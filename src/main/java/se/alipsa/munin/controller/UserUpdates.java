package se.alipsa.munin.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

public class UserUpdates {

  private List<UserUpdate> updateList = new ArrayList<>();

  public UserUpdates() {
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public UserUpdates(List<UserUpdate> updateList) {
    this.updateList = updateList;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<UserUpdate> getUpdateList() {
    return updateList;
  }

  public void addUserUpdate(UserUpdate userUpdate) {
    updateList.add(userUpdate);
  }
}
