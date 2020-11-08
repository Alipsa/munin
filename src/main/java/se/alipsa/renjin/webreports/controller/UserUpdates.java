package se.alipsa.renjin.webreports.controller;

import java.util.ArrayList;
import java.util.List;

public class UserUpdates {

  private List<UserUpdate> updateList = new ArrayList<>();

  public UserUpdates() {
  }

  public UserUpdates(List<UserUpdate> updateList) {
    this.updateList = updateList;
  }

  public List<UserUpdate> getUpdateList() {
    return updateList;
  }

  public void addUserUpdate(UserUpdate userUpdate) {
    updateList.add(userUpdate);
  }
}
