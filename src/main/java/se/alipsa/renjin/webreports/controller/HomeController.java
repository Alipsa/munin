package se.alipsa.renjin.webreports.controller;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.alipsa.renjin.webreports.model.Report;
import se.alipsa.renjin.webreports.repo.ReportRepo;
import se.alipsa.renjin.webreports.repo.UserRepo;
import se.alipsa.renjin.webreports.service.UserRoleService;
import se.alipsa.renjin.webreports.util.PasswordGenerator;

import java.util.List;

@Controller
public class HomeController {

  private final ReportRepo reportRepo;
  private final UserRoleService userRoleService;

  @Autowired
  public HomeController( ReportRepo reportRepo, UserRoleService userRoleService) {
    this.reportRepo = reportRepo;
    this.userRoleService = userRoleService;
  }

  @GetMapping(path = "/")
  public ModelAndView home() {
    ModelAndView mav = new ModelAndView();
    List<Report> reportList = IterableUtils.toList(reportRepo.findAll());
    mav.addObject("reportList", reportList);
    mav.setViewName("index");
    return mav;
  }

  @GetMapping(path = "/error")
  public String error(Model model) {
    return "error";
  }

  // Login form
  @GetMapping("/login.html")
  public String loginForm() {
    return "login";
  }

  // Login form with error
  @GetMapping("/login-error.html")
  public String loginError(Model model) {
    model.addAttribute("loginError", true);
    return "login";
  }

  @GetMapping("/resetPassword")
  public String resetPasswordForm() {
    return "resetPasswordForm";
  }

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
