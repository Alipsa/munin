package se.alipsa.renjin.webreports.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

  @GetMapping("/manage/addReport")
  public String listReports() {
    return "addReport";
  }
}
