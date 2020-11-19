package se.alipsa.renjin.webreports.controller;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import se.alipsa.renjin.webreports.model.Report;
import se.alipsa.renjin.webreports.repo.ReportRepo;
import se.alipsa.renjin.webreports.service.ReportDefinitionException;
import se.alipsa.renjin.webreports.service.ReportEngine;
import se.alipsa.renjin.webreports.service.ReportSchedulerService;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ReportController {

  //private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
  private final ReportRepo reportRepo;
  private final ReportEngine reportEngine;
  private final ReportSchedulerService reportSchedulerService;

  @Autowired
  public ReportController(ReportRepo reportRepo, ReportEngine reportEngine, ReportSchedulerService reportSchedulerService) {
    this.reportRepo = reportRepo;
    this.reportEngine = reportEngine;
    this.reportSchedulerService = reportSchedulerService;
  }

  @GetMapping("/viewReport/{name}")
  public String viewReport(@PathVariable String name, Model model) throws ReportNotFoundException, ScriptException, ReportDefinitionException {
    Report report = loadReport(name);
    model.addAttribute("reportName", name);
    model.addAttribute("reportDescription", report.getDescription());
    if (report.getInputContent() == null || report.getInputContent().trim().isEmpty()){
      String reportContent = reportEngine.runReport(report.getDefinition());
      model.addAttribute(report.getInputContent());
      model.addAttribute("reportContent", reportContent);
      return "viewReport";
    }
    model.addAttribute("inputContent", report.getInputContent());
    return "reportInputForm";
  }

  @PostMapping(path = "/viewReport/{name}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ModelAndView viewReport(@PathVariable String name, @RequestParam MultiValueMap<String, List<?>> paramMap) throws ScriptException, ReportDefinitionException, ReportNotFoundException {
    ModelAndView mav = new ModelAndView();
    Report report = loadReport(name);
    mav.addObject("reportName", name);
    mav.addObject("reportDescription", report.getDescription());
    Map<String, Object> params = new HashMap<>();
    // Unpack the list if it contains a single value
    paramMap.forEach((k,v) -> {
      if (v.size() == 1) {
        params.put(k, v.get(0));
      } else {
        params.put(k, v);
      }
    });
    String reportContent = reportEngine.runReport(report.getDefinition(), params);

    mav.addObject(report.getInputContent());
    mav.addObject("reportContent", reportContent);
    mav.setViewName("viewReport");
    return mav;
  }

  private Report loadReport(String name) throws ReportNotFoundException {
    Optional<Report> dbReport = reportRepo.findById(name);
    if (!dbReport.isPresent()) {
      throw new ReportNotFoundException("There is no report with the name " + name);
    }
    return dbReport.get();
  }

  @GetMapping("/manage/addReport")
  public String addReportForm() {
    return "addReport";
  }

  @PostMapping(path = "/manage/addReport", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public RedirectView addReport(@RequestParam String reportName, @RequestParam String description,
                                @RequestParam String definition, @RequestParam String inputContent,
                                RedirectAttributes redirectAttributes) {
    Report report = new Report();
    report.setReportName(reportName);
    report.setDescription(description);
    report.setDefinition(definition);
    report.setInputContent(inputContent);
    reportRepo.save(report);
    redirectAttributes.addFlashAttribute("message",reportName + " added successfully!");
    return new RedirectView("/");
  }

  @GetMapping("/manage/schedule")
  public ModelAndView scheduleReportForm() {
    ModelAndView mav = new ModelAndView();
    List<String> reportNames = new ArrayList<>();
    reportRepo.findAll().forEach(r -> reportNames.add(r.getReportName()));
    mav.addObject("reportList", reportNames);
    mav.setViewName("scheduleReport");
    return mav;
  }

  @PostMapping(path = "/manage/schedule", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public RedirectView scheduleReport(@RequestParam String reportName, @RequestParam String cronVal,
                                     @RequestParam String emails, RedirectAttributes redirectAttributes) {
    String[] reportRecepients = emails.replace(',', ';').split(";");
    reportSchedulerService.scheduleReport(reportName, cronVal, reportRecepients);
    redirectAttributes.addFlashAttribute("message",reportName + " scheduled successfully!");
    return new RedirectView("/");
  }

  @GetMapping(path = "/manage/editReport/{name}")
  public String editReportForm(@PathVariable String name, Model model) throws ReportNotFoundException {
    Report report = loadReport(name);
    model.addAttribute("reportName", name);
    model.addAttribute("reportDescription", report.getDescription());
    model.addAttribute("definition", report.getDefinition());
    model.addAttribute("inputContent", report.getInputContent());
    return "editReport";
  }

  @PostMapping(path = "/manage/editReport")
  public RedirectView modifyReport(@RequestParam String reportName, @RequestParam String description,
                             @RequestParam String definition, @RequestParam String inputContent,
                             RedirectAttributes redirectAttributes) throws ReportNotFoundException {
    Report report = loadReport(reportName);
    report.setDescription(description);
    report.setDefinition(definition);
    report.setInputContent(inputContent);
    reportRepo.save(report);
    redirectAttributes.addFlashAttribute("message",reportName + " modified successfully!");
    return new RedirectView("/");
  }

  @GetMapping(path = "/manage/deleteReport/{name}")
  public RedirectView deleteReport(@PathVariable String name, RedirectAttributes redirectAttributes) throws ReportNotFoundException {
    Report report = loadReport(name);
    reportRepo.delete(report);
    redirectAttributes.addFlashAttribute("message",name + " deleted successfully!");
    return new RedirectView("/");
  }

}
