package se.alipsa.munin.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.alipsa.munin.model.web.ReportGroupInfo;
import se.alipsa.munin.repo.ReportRepo;
import se.alipsa.munin.service.UserRoleService;

import java.util.List;

/**
 * Controller for handling home page and login related requests.
 */
@Controller
public class HomeController {

  private final ReportRepo reportRepo;
  private final UserRoleService userRoleService;

  /**
   * Constructor with autowired dependencies.
   *
   * @param reportRepo the report repository
   * @param userRoleService the user role service
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public HomeController( ReportRepo reportRepo, UserRoleService userRoleService) {
    this.reportRepo = reportRepo;
    this.userRoleService = userRoleService;
  }

  /**
   * Show the home page with the available report groups.
   *
   * @return the model and view for the home page
   */
  @GetMapping(path = "/")
  public ModelAndView home() {
    ModelAndView mav = new ModelAndView();
    List<ReportGroupInfo> groupList = reportRepo.getGroupInfo();
    mav.addObject("reportGroups", groupList);
    mav.setViewName("index");
    return mav;
  }

  /**
   * Show a generic error page.
   *
   * @param model the model
   * @return the name of the error view
   */
  @GetMapping(path = "/error")
  public String error(Model model) {
    return "error";
  }

  /**
   * Show the login form.
   *
   * @return the name of the login form view
   */
  @GetMapping("/login.html")
  public String loginForm() {
    return "login";
  }

  /**
   * Show the login form with an error message if login failed.
   *
   * @param model the model
   * @return the name of the login form view
   */
  @GetMapping("/login-error.html")
  public String loginError(Model model) {
    model.addAttribute("loginError", true);
    return "login";
  }

  /**
   * Show the reset password form.
   *
   * @return the name of the reset password form view
   */
  @GetMapping("/resetPassword")
  public String resetPasswordForm() {
    return "resetPasswordForm";
  }

  /**
   * Handle the reset password request. If the username and email match, a new password is generated
   * and emailed to the user.
   *
   * @param username the username of the user
   * @param email the email of the user
   * @param redirectAttributes attributes for flash messages
   * @return redirect to the login page with a message
   */
  @PostMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String resetPassword(@RequestParam String username, @RequestParam String email,
                              RedirectAttributes redirectAttributes) {
    if (userRoleService.verify(username, email)) {
      userRoleService.resetPassword(username);
      redirectAttributes.addFlashAttribute("message", "A new password was emailed to you");
      return "redirect:/login.html";
    } else {
      redirectAttributes.addFlashAttribute("message", "username does not exist or email did not match!");
      return "redirect:/resetPassword";
    }
  }
}
