package se.alipsa.renjin.webreports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.alipsa.renjin.webreports.controller.AddUserException;
import se.alipsa.renjin.webreports.controller.UserUpdate;
import se.alipsa.renjin.webreports.model.Authorities;
import se.alipsa.renjin.webreports.model.AuthoritiesPk;
import se.alipsa.renjin.webreports.model.User;
import se.alipsa.renjin.webreports.repo.AuthoritiesRepo;
import se.alipsa.renjin.webreports.repo.UserRepo;
import se.alipsa.renjin.webreports.util.PasswordGenerator;

import java.util.*;

import static se.alipsa.renjin.webreports.config.Role.ROLE_ADMIN;
import static se.alipsa.renjin.webreports.config.Role.ROLE_ANALYST;
import static se.alipsa.renjin.webreports.config.Role.ROLE_VIEWER;

@Service
public class UserRoleService {

  private final UserRepo userRepo;
  private final AuthoritiesRepo authoritiesRepo;
  private final EmailService emailService;

  @Value("${webreports.password-length:10}")
  int passwordLength;

  @Autowired
  public UserRoleService(UserRepo userRepo, AuthoritiesRepo authoritiesRepo, EmailService emailService) {
    this.userRepo = userRepo;
    this.authoritiesRepo = authoritiesRepo;
    this.emailService = emailService;
  }

  @Transactional
  public List<UserUpdate> fetchUserUpdates() {
    List<UserUpdate> users = new ArrayList<>();
    userRepo.findAll().forEach(u -> {
      UserUpdate uu = new UserUpdate();
      uu.setUsername(u.getUsername());
      uu.setEnabled(u.isEnabled());
      uu.setFailedAttempts(u.getFailedAttempts());
      uu.setViewer(u.isViewer());
      uu.setAnalyst(u.isAnalyst());
      uu.setAdmin(u.isAdmin());
      users.add(uu);
    });

    return users;
  }

  @Transactional
  public void updateUsers(List<UserUpdate> updateList) {
    Map<String, User> map = new HashMap<>();
    userRepo.findAll().forEach(u -> map.put(u.getUsername(), u));
    updateList.forEach(update -> {
      User u = map.get(update.getUsername());
      if (u != null) {
        System.out.println("Updating user " + update.getUsername());
        u.setEnabled(update.isEnabled());
        u.setEmail(update.getEmail());
        userRepo.save(u); // Strange that this is needed, we are in a transaction
        authoritiesRepo.deleteByUser(u);
        addRoles(update, u);
      }
    });
  }

  private void addRoles(UserUpdate update, User u) {
    List<Authorities> authToUpdate = new ArrayList<>();
    if (update.isAdmin()) {
      Authorities auth = new Authorities(new AuthoritiesPk(u, ROLE_ADMIN.name()));
      System.out.println("Add ROLE_ADMIN role");
      authToUpdate.add(auth);
      //authoritiesRepo.save(auth);
    }
    if (update.isAnalyst()) {
      Authorities auth = new Authorities(new AuthoritiesPk(u, ROLE_ANALYST.name()));
      System.out.println("Add ROLE_ANALYST role");
      //authoritiesRepo.save(auth);
      authToUpdate.add(auth);
    }
    if (update.isViewer()) {
      Authorities auth = new Authorities(new AuthoritiesPk(u, ROLE_VIEWER.name()));
      System.out.println("Add ROLE_VIEWER role");
      //authoritiesRepo.save(auth);
      authToUpdate.add(auth);
    }
    if (authToUpdate.size() > 0) {
      authoritiesRepo.saveAll(authToUpdate);
    }
  }

  @Transactional
  public void addUser(UserUpdate userUpdate, String encodedPwd) throws AddUserException {
    Optional<User> userOpt = userRepo.findById(userUpdate.getUsername());
    if (userOpt.isPresent()) {
      throw new AddUserException(userUpdate.getUsername() + " already exists");
    }
    User user = new User();
    user.setUsername(userUpdate.getUsername());
    user.setEmail(userUpdate.getEmail());
    user.setPassword(encodedPwd);
    user.setEnabled(true);
    user.setFailedAttempts(0);

    User dbUser = userRepo.save(user);
    addRoles(userUpdate, dbUser);
  }

  @Transactional
  public boolean verify(String username, String email) {
    Optional<User> userOpt = userRepo.findById(username);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      return nullBlankLc(user.getEmail()).equals(nullBlankLc(email));
    }
    return false;
  }

  private String nullBlankLc(String str) {
    String val = str == null ? "" : str;
    return val.toLowerCase();
  }

  @Transactional
  @Modifying
  public void resetPassword(String username) {
    Optional<User> userOpt = userRepo.findById(username);
    if (!userOpt.isPresent()) {
      throw new IllegalArgumentException("user " + username + " does not exist");
    }
    User user = userOpt.get();

    String passwd = PasswordGenerator.generateRandomPassword(passwordLength);
    String encodedPwd = PasswordGenerator.encrypt(passwd);
    user.setPassword(encodedPwd);
    //userRepo.save(user); // should not be needed, we are in a transaction
    emailService.send(user.getEmail(), "New password for Renjin Web Reports",
        "Welcome back to Renjin Web reports\n Your new password is password is " + passwd);
  }
}
