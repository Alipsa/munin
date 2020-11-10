package se.alipsa.renjin.webreports.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.alipsa.renjin.webreports.service.EmailService;
import se.alipsa.renjin.webreports.service.UserRoleService;
import se.alipsa.renjin.webreports.util.PasswordGenerator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

@Controller
public class AdminController {

  @Value("${webreports.password-length:10}")
  int passwordLength;

  private final UserRoleService userRoleService;
  private final EmailService emailService;

  public AdminController(UserRoleService userRoleService, EmailService emailService) {
    this.userRoleService = userRoleService;
    this.emailService = emailService;
  }

  @GetMapping("/admin/index")
  public String adminIndex(Model model) {
    List<UserUpdate> users = userRoleService.fetchUserUpdates();
    model.addAttribute("users", new UserUpdates(users));
    return "adminIndex";
  }

  @PostMapping("/admin/updateUsers")
  public String updateUsers(@ModelAttribute UserUpdates userUpdates, RedirectAttributes redirectAttributes) {
    userRoleService.updateUsers(userUpdates.getUpdateList());
    redirectAttributes.addFlashAttribute("message","users updated successfully!");
    return "redirect:/admin/index";
  }

  @PostMapping("/admin/addUser")
  public String addUser(@ModelAttribute UserUpdate userUpdate, RedirectAttributes redirectAttributes) {
    if (userUpdate == null) {
      throw new IllegalArgumentException("userUpdate object is null");
    }
    if (!isValidEmail(userUpdate.getEmail())) {
      throw new IllegalArgumentException("Email address is invalid");
    }
    if (userUpdate.getUsername() == null || userUpdate.getUsername().length() < 3 ) {
      throw new IllegalArgumentException("Username is missing or too short (< 3 characters)");
    }
    String passwd = PasswordGenerator.generateRandomPassword(passwordLength);
    String encodedPwd = PasswordGenerator.encrypt(passwd);
    try {
      userRoleService.addUser(userUpdate, encodedPwd);
    } catch (AddUserException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    emailService.send(userUpdate.getEmail(), "User details for Renjin Web Reports",
        "Welcome to Renjin Web reports\n Your username is " + userUpdate.getUsername()
            + "\nand password is " + passwd);
    redirectAttributes.addFlashAttribute("message","user " + userUpdate.getUsername() + " added successfully and email sent!");
    return "redirect:/admin/index";
  }


  public static boolean isValidEmail(String email) {
    boolean result = true;
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }

}
