package se.alipsa.munin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.repo.ReportRepo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class RestApiController {

  private static final Logger LOG = LogManager.getLogger();

  private final ReportRepo reportRepo;

  private final ObjectMapper mapper;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public RestApiController(ReportRepo reportRepo, ObjectMapper mapper) {
    this.reportRepo = reportRepo;
    this.mapper = mapper;
  }

  @GetMapping(value = "/api/getReportGroups", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getReportGroups() {
    return reportRepo.getReportGroups();
  }

  @GetMapping(value = "/api/getReports", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Report> getReports(@RequestParam String groupName) {
    return reportRepo.findByReportGroupOrderByReportName(groupName);
  }

  @GetMapping(value = "/api/getReportInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<String>> getReportInfo() {
    Map<String, List<String>> map = new HashMap<>();
    for (Report report : reportRepo.findAll()) {
      map.computeIfAbsent(report.getReportGroup(), k -> new ArrayList<>());
      map.get(report.getReportGroup()).add(report.getReportName());
    }
    return map;
  }

  @GetMapping(value = "/api/getReport", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody Report getReport(@RequestParam String name) {
    var decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
    try {
      return reportRepo.loadReport(decoded);
    } catch (ReportNotFoundException e) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Report name '" + decoded + "' cannot be found");
    }
  }

  @PostMapping(value = "/api/addReport", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void addReport(@RequestBody Report report) {
    if (report.getReportName() == null || "".equals(report.getReportName().trim())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (reportRepo.findById(report.getReportName()).isPresent()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "There is already a report with that name");
    }
    if (report.getTemplate() == null || "".equals(report.getTemplate().trim())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Report definition (R code) cannot be empty");
    }
    if (report.getReportGroup() == null || report.getReportGroup().trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      report.setReportGroup("None");
    }
    reportRepo.save(report);
  }

  /*@RequestMapping(value = "/api/updateReport", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public void updateReportAny(@RequestBody String report, HttpServletRequest request) {
    LOG.info("got call to /api/updateReport, method = {}, mediatype = {}",
        request.getMethod(), request.getHeader(CONTENT_TYPE));
    try {
      updateReport(mapper.readValue(report, Report.class));
    } catch (JsonProcessingException e) {
      LOG.warn("Failed to deserialize the report", e);
      throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to deserialize the report: " + e);
    }
  }*/

  @PutMapping(value = "/api/updateReport", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateReport(@RequestBody Report report) {
    LOG.info("Updating report: {}", report);
    if (report.getReportName() == null || "".equals(report.getReportName().trim())) {
      LOG.warn("updateReport: Report name cannot be empty");
      throw new ApiException(HttpStatus.BAD_REQUEST, "Report name cannot be empty");
    }
    if (report.getTemplate() == null || "".equals(report.getTemplate().trim())) {
      LOG.warn("updateReport: Report definition (code) cannot be empty");
      throw new ApiException(HttpStatus.BAD_REQUEST, "Report definition (code) cannot be empty");
    }
    if (report.getReportGroup() == null || report.getReportGroup().trim().isEmpty()) {
      LOG.warn("Report Group is blank, setting it to None");
      report.setReportGroup("None");
    }
    try {
      Report dbReport = reportRepo.loadReport(report.getReportName());
      dbReport.setDescription(report.getDescription());
      dbReport.setTemplate(report.getTemplate());
      dbReport.setInputContent(report.getInputContent());
      dbReport.setReportType(report.getReportType());
      dbReport.setReportGroup(report.getReportGroup());
      reportRepo.save(report);
    } catch (ReportNotFoundException e) {
      LOG.warn("Failed to save report", e);
      throw new ApiException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
