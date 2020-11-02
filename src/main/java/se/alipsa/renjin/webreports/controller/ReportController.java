package se.alipsa.renjin.webreports.controller;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.alipsa.renjin.webreports.model.Report;
import se.alipsa.renjin.webreports.repo.ReportRepo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class ReportController {

  private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
  private final ReportRepo reportRepo;

  @Autowired
  public ReportController( ReportRepo reportRepo) {
    this.reportRepo = reportRepo;
  }

  @GetMapping("/manage/addReport")
  public String addReportForm() {
    return "addReport";
  }

  @PostMapping(value = "/manage/addReport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String addReport(@RequestParam String reportName, @RequestParam String description,
                          @RequestParam MultipartFile file, @RequestParam String inputContent,
                          RedirectAttributes redirectAttributes) {
    try {
      String content = new String(file.getBytes(), StandardCharsets.UTF_8);
      Report report = new Report();
      report.setReportName(reportName);
      report.setDescription(description);
      report.setReportContent(content);
      report.setInputContent(inputContent);
      reportRepo.save(report);
      redirectAttributes.addFlashAttribute("message","Report uploaded successfully!");
    } catch (IOException e) {
      LOG.warn("Failed to process data", e);
      redirectAttributes.addFlashAttribute("message","Upload failed! " + e);
    }
    return "addReport";
  }
}
