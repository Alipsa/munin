package se.alipsa.munin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.repo.ReportRepo;

import java.util.List;

@RestController
public class RestApiController {

  private static final Logger LOG = LogManager.getLogger();

  private final ReportRepo reportRepo;

  @Autowired
  public RestApiController(ReportRepo reportRepo) {
    this.reportRepo = reportRepo;
  }

  @GetMapping(value = "/api/getReportGroups", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getReportGroups() {
    return reportRepo.getReportGroups();
  }

  @GetMapping(value = "/api/getReports", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Report> getReports(@RequestParam String groupName) {
    return reportRepo.findByReportGroupOrderByReportName(groupName);
  }

  @PostMapping(value = "/api/addReport", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void addReport(@RequestBody Report report) {
    if (report.getReportName() == null || "".equals(report.getReportName().trim())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (reportRepo.findById(report.getReportName()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already a report with that name");
    }
    if (report.getDefinition() == null || "".equals(report.getDefinition().trim())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report definition (R code) cannot be empty");
    }
    if (report.getReportGroup() == null || report.getReportGroup().trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      report.setReportGroup("None");
    }
    reportRepo.save(report);
  }

  @PutMapping(value = "/api/updateReport", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateReport(@RequestBody Report report) {
    LOG.debug("Updating report: {}", report);
    if (report.getReportName() == null || "".equals(report.getReportName().trim())) {
      LOG.warn("updateReport: Report name cannot be empty");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (report.getDefinition() == null || "".equals(report.getDefinition().trim())) {
      LOG.warn("updateReport: Report definition (R code) cannot be empty");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report definition (R code) cannot be empty");
    }
    if (report.getReportGroup() == null || report.getReportGroup().trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      report.setReportGroup("None");
    }
    try {
      Report dbReport = reportRepo.loadReport(report.getReportName());
      dbReport.setDescription(report.getDescription());
      dbReport.setDefinition(report.getDefinition());
      dbReport.setInputContent(report.getInputContent());
      dbReport.setReportType(report.getReportType());
      dbReport.setReportGroup(report.getReportGroup());
      reportRepo.save(report);
    } catch (ReportNotFoundException e) {
      LOG.warn("Failed to save report", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
