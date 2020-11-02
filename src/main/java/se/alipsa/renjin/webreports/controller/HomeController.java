package se.alipsa.renjin.webreports.controller;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import se.alipsa.renjin.webreports.model.Report;
import se.alipsa.renjin.webreports.repo.ReportRepo;

import java.util.List;

@Controller
public class HomeController {

  private final ReportRepo reportRepo;

  @Autowired
  public HomeController( ReportRepo reportRepo) {
    this.reportRepo = reportRepo;
  }

  @GetMapping(path = "/")
  public ModelAndView home() {
    ModelAndView mav = new ModelAndView();
    List<Report> reportList = IterableUtils.toList(reportRepo.findAll());
    mav.addObject("reportList", reportList);
    mav.setViewName("index");
    return mav;
  }

  // Login form
  @RequestMapping("/login.html")
  public String loginForm() {
    return "login";
  }

  // Login form with error
  @RequestMapping("/login-error.html")
  public String loginError(Model model) {
    model.addAttribute("loginError", true);
    return "login";
  }
}
