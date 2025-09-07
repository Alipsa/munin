package se.alipsa.munin.controller;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.parser.CronParser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import se.alipsa.gmd.core.GmdException;
import se.alipsa.journo.JournoException;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.model.ReportSchedule;
import se.alipsa.munin.model.ReportType;
import se.alipsa.munin.model.web.ReportScheduleWeb;
import se.alipsa.munin.model.web.ReportScheduleWebFactory;
import se.alipsa.munin.repo.ReportRepo;
import se.alipsa.munin.repo.ReportScheduleRepo;
import se.alipsa.munin.service.ReportDefinitionException;
import se.alipsa.munin.service.ReportSchedulerService;
import se.alipsa.munin.service.ReportService;

import javax.script.ScriptException;
import java.util.*;

/**
 * Controller for handling report-related requests such as viewing, adding, editing,
 * deleting, and scheduling reports.
 */
@Controller
public class ReportController {

  private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
  private final ReportRepo reportRepo;
  private final ReportScheduleRepo reportScheduleRepo;

  private final ReportService reportService;
  private final ReportSchedulerService reportSchedulerService;
  private final ReportScheduleWebFactory reportScheduleWebFactory;

  private final CronDescriptor descriptor = CronDescriptor.instance();
  private final CronParser cronParser;

  /**
   * Constructor with autowired dependencies.
   *
   * @param reportRepo            the report repository
   * @param reportScheduleRepo    the report schedule repository
   * @param reportService         the report service
   * @param reportSchedulerService the report scheduler service
   * @param reportScheduleWebFactory the factory for creating web representations of report schedules
   * @param cronParser            the cron expression parser
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public ReportController(ReportRepo reportRepo, ReportScheduleRepo reportScheduleRepo, ReportService reportService,
                          ReportSchedulerService reportSchedulerService, ReportScheduleWebFactory reportScheduleWebFactory,
                          @Qualifier("springCronParser") CronParser cronParser) {
    this.reportRepo = reportRepo;
    this.reportScheduleRepo = reportScheduleRepo;
    this.reportService = reportService;
    this.reportSchedulerService = reportSchedulerService;
    this.reportScheduleWebFactory = reportScheduleWebFactory;
    this.cronParser = cronParser;
  }

  /**
   * Show the reports in a specific group.
   *
   * @param group the name of the report group
   * @return the model and view for the reports in the group
   */
  @GetMapping(path = "/reports/{group}")
  public ModelAndView reportsInGroup(@PathVariable String group) {
    ModelAndView mav = new ModelAndView();
    List<Report> reportList = reportRepo.findByReportGroupOrderByReportName(group);
    mav.addObject("reportList", reportList);
    mav.addObject("reportGroup", group);
    mav.setViewName("reportsInGroup");
    return mav;
  }

  /**
   * View a report. If the report requires parameters an input form is shown first.
   *
   * @param name  the name of the report
   * @param model the model to populate
   * @return the name of the view for displaying the report or the input form
   * @throws ReportNotFoundException  if the report with the given name was not found
   * @throws ScriptException         if the script engine failed
   * @throws ReportDefinitionException if the report is not well defined
   */
  @GetMapping("/viewReport/{name}")
  public String viewReport(@PathVariable String name, Model model) throws ReportNotFoundException, ScriptException, ReportDefinitionException {
    LOG.debug("Running report '{}'", name);
    Report report = reportRepo.loadReport(name);
    model.addAttribute("reportName", name);
    model.addAttribute("reportGroup", report.getReportGroup());
    model.addAttribute("reportDescription", report.getDescription());
    if (report.getInputContent() == null || report.getInputContent().trim().isEmpty()){
      String reportContent;
      try {
        reportContent = reportService.runReport(report);
      } catch (GmdException | JournoException e) {
        LOG.warn("Failed to run report", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to run report", e);
      }
      model.addAttribute(report.getInputContent());
      model.addAttribute("reportContent", reportContent);
      return "viewReport";
    }
    model.addAttribute("inputContent", report.getInputContent());
    return "reportInputForm";
  }

  /**
   * View a report that requires parameters.
   *
   * @param name     the name of the report
   * @param paramMap the parameters from the input form
   * @return the model and view for the report
   * @throws ScriptException         if the script engine failed
   * @throws ReportDefinitionException if the report is not well defined
   * @throws ReportNotFoundException  if the report with the given name was not found
   */
  @PostMapping(path = "/viewReport/{name}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ModelAndView viewReport(@PathVariable String name, @RequestParam MultiValueMap<String, List<?>> paramMap) throws ScriptException, ReportDefinitionException, ReportNotFoundException {
    ModelAndView mav = new ModelAndView();
    Report report = reportRepo.loadReport(name);
    mav.addObject("reportName", name);
    mav.addObject("reportGroup", report.getReportGroup());
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
    String reportContent;
    try {
      reportContent = reportService.runReport(report, params);
    } catch (GmdException | JournoException e) {
      LOG.warn("Failed to run parameterized report", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to run report", e);
    }
    mav.addObject(report.getInputContent());
    mav.addObject("reportContent", reportContent);
    mav.setViewName("viewReport");
    return mav;
  }

  /**
   * Show the form for adding a new report.
   *
   * @param model the model to populate
   * @return the name of the view for adding or editing a report
   */
  @GetMapping("/manage/addReport")
  public String addReportForm(Model model) {
    model.addAttribute("reportGroups", reportRepo.getReportGroups());
    model.addAttribute("action", "addReport");
    return "addOrEditReport";
  }

  /**
   * Add a new report based on the submitted form data.
   *
   * @param reportName        the name of the report
   * @param description       the description of the report
   * @param inputContent     the input content for the report
   * @param preProcessing    the pre-processing code for the report
   * @param template          the template code for generating the report
   * @param reportType       the type of the report
   * @param reportGroup      the group to which the report belongs
   * @param redirectAttributes attributes for redirecting with messages
   * @return a redirect view to the home page
   */
  @PostMapping(path = "/manage/addReport", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public RedirectView addReport(@RequestParam String reportName, @RequestParam String description,
                                @RequestParam String inputContent, @RequestParam String preProcessing,
                                @RequestParam String template,
                                @RequestParam ReportType reportType, @RequestParam String reportGroup,
                                RedirectAttributes redirectAttributes) {
    if (reportName == null || reportName.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (reportRepo.findById(reportName).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already a report with that name");
    }
    if (template == null || template.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report template (html generating code) cannot be empty");
    }
    if (reportGroup == null || reportGroup.trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      reportGroup = "None";
    }
    Report report = new Report();
    report.setReportName(reportName);
    report.setDescription(description);
    report.setInputContent(inputContent);
    report.setPreProcessing(preProcessing);
    report.setTemplate(template);

    report.setReportType(reportType);
    report.setReportGroup(reportGroup);
    reportRepo.save(report);
    redirectAttributes.addFlashAttribute("message",reportName + " added successfully!");
    return new RedirectView("/");
  }

  /**
   * Show the form for editing an existing report.
   *
   * @param name  the name of the report to edit
   * @param model the model to populate
   * @return the name of the view for adding or editing a report
   * @throws ReportNotFoundException if the report with the given name was not found
   */
  @GetMapping(path = "/manage/editReport/{name}")
  public String editReportForm(@PathVariable String name, Model model) throws ReportNotFoundException {
    populateEditReportModel(name, model);
    model.addAttribute("action", "editReport");
    return "addOrEditReport";
  }

  /**
   * This mapping is kept for backward compatibility with old links/bookmarks
   *
   * @param name  the name of the report to edit
   * @param model the model to populate
   * @return the name of the view for editing a report
   * @throws ReportNotFoundException if the report with the given name was not found
   */
  @GetMapping(path = "/manage/oldEditReport/{name}")
  public String oldEditReportForm(@PathVariable String name, Model model) throws ReportNotFoundException {
    populateEditReportModel(name, model);
    return "editReport";
  }

  /**
   * Populate the model with the report details for editing.
   *
   * @param name  the name of the report to edit
   * @param model the model to populate
   * @throws ReportNotFoundException if the report with the given name was not found
   */
  private void populateEditReportModel(String name, Model model) throws ReportNotFoundException {
    Report report = reportRepo.loadReport(name);
    model.addAttribute("reportName", name);
    model.addAttribute("reportDescription", report.getDescription());
    model.addAttribute("preProcessing", report.getPreProcessing());
    model.addAttribute("template", report.getTemplate());
    model.addAttribute("inputContent", report.getInputContent());
    model.addAttribute("reportType", report.getReportType());
    model.addAttribute("reportGroup", report.getReportGroup());
    model.addAttribute("reportGroups", reportRepo.getReportGroups());
  }

  /**
   * Modify an existing report based on the submitted form data.
   *
   * @param reportName        the name of the report
   * @param description       the description of the report
   * @param inputContent     the input content for the report
   * @param preProcessing    the pre-processing code for the report
   * @param template          the template code for generating the report
   * @param reportType       the type of the report
   * @param reportGroup      the group to which the report belongs
   * @param redirectAttributes attributes for redirecting with messages
   * @return a redirect view to the reports in the same group
   * @throws ReportNotFoundException if the report with the given name was not found
   */
  @PostMapping(path = "/manage/editReport")
  public RedirectView modifyReport(@RequestParam String reportName, @RequestParam String description,
                                   @RequestParam String inputContent, @RequestParam String preProcessing,
                                   @RequestParam String template, @RequestParam ReportType reportType,
                                   @RequestParam String reportGroup,
                                   RedirectAttributes redirectAttributes) throws ReportNotFoundException {
    if (reportName == null || reportName.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (template == null || template.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report template (html generating code) cannot be empty");
    }
    if (reportGroup == null || reportGroup.trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      reportGroup = "None";
    }
    Report report = reportRepo.loadReport(reportName);
    report.setDescription(description);
    report.setInputContent(inputContent);
    report.setPreProcessing(preProcessing);
    report.setTemplate(template);
    report.setReportType(reportType);
    report.setReportGroup(reportGroup);
    reportRepo.save(report);
    redirectAttributes.addFlashAttribute("message",reportName + " modified successfully!");
    return new RedirectView("/reports/" + reportGroup);
  }

  /**
   * Delete an existing report.
   *
   * @param name  the name of the report to delete
   * @param redirectAttributes attributes for redirecting with messages
   * @return a redirect view to the home page
   * @throws ReportNotFoundException if the report with the given name was not found
   */
  @GetMapping(path = "/manage/deleteReport/{name}")
  public RedirectView deleteReport(@PathVariable String name, RedirectAttributes redirectAttributes) throws ReportNotFoundException {
    Report report = reportRepo.loadReport(name);
    reportRepo.delete(report);
    redirectAttributes.addFlashAttribute("message",name + " deleted successfully!");
    return new RedirectView("/");
  }

  /**
   * Show the form for scheduling reports.
   *
   * @return the model and view for scheduling reports
   */
  @GetMapping("/manage/schedule")
  public ModelAndView scheduleReportForm() {
    ModelAndView mav = new ModelAndView();
    List<String> reportNames = new ArrayList<>();
    reportRepo.findAll().forEach(r -> reportNames.add(r.getReportName()));
    mav.addObject("reportList", reportNames);

    List<ReportScheduleWeb> schedules = new ArrayList<>();
    reportScheduleRepo.findAll().forEach(s -> schedules.add(reportScheduleWebFactory.create(s)));
    Collections.sort(schedules);
    mav.addObject("schedules", schedules);
    mav.setViewName("scheduleReport");
    return mav;
  }

  /**
   * Schedule a new report or update an existing report schedule.
   *
   * @param id          the id of the report schedule to update (null for new schedules)
   * @param reportName  the name of the report to schedule
   * @param cronVal     the cron expression for scheduling
   * @param emails      the email addresses to send the report to
   * @param redirectAttributes attributes for redirecting with messages
   * @return a redirect view to the schedule management page
   */
  @PostMapping(path = "/manage/schedule", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public RedirectView scheduleReport(@RequestParam(required = false) Long id,
                                     @RequestParam(required = false) String reportName, @RequestParam String cronVal,
                                     @RequestParam String emails, RedirectAttributes redirectAttributes) {
    emails = emails.replace(',', ';');
    Cron cron = cronParser.parse(cronVal);
    ReportSchedule schedule = new ReportSchedule(reportName, cron.asString(), emails);
    if (id == null) {
      reportSchedulerService.addReportSchedule(schedule);
      LOG.info("{} scheduled successfully to run {}", reportName, descriptor.describe(cron));
      redirectAttributes.addFlashAttribute("message",reportName + " scheduled successfully!");
    } else {
      reportName = reportSchedulerService.updateReportSchedule(id, schedule);
      LOG.info("{} schedule updated successfully to run {}", reportName, descriptor.describe(cron));
      redirectAttributes.addFlashAttribute("message",reportName + " schedule updated successfully!");
    }
    return new RedirectView("/manage/schedule");
  }

  /**
   * Delete an existing report schedule.
   *
   * @param id the id of the report schedule to delete
   * @param redirectAttributes attributes for redirecting with messages
   * @return a redirect view to the schedule management page
   */
  @GetMapping(path = "/manage/schedule/delete/{id}")
  public RedirectView deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    ReportSchedule schedule = reportSchedulerService.deleteReportSchedule(id);
    String reportName = schedule.getReportName();
    String cron = schedule.getCron();
    LOG.info("Schedule for {} with schedule {} deleted", reportName, descriptor.describe(cronParser.parse(cron)));
    redirectAttributes.addFlashAttribute("message","Schedule for report " + reportName + " with schedule " + cron + " deleted successfully!");
    return new RedirectView("/manage/schedule");
  }
}
