package se.alipsa.renjin.webreports.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import se.alipsa.renjin.webreports.model.Report;
import se.alipsa.renjin.webreports.repo.ReportRepo;

import javax.mail.MessagingException;
import javax.script.ScriptException;
import java.util.Optional;

@Service
public class ReportSchedulerService {

  private final TaskScheduler executor;
  private final ReportRepo reportRepo;
  private final ReportEngine reportEngine;
  private final EmailService emailService;

  private final static Logger LOG = LoggerFactory.getLogger(ReportSchedulerService.class);

  @Autowired
  public ReportSchedulerService(TaskScheduler executor, ReportRepo reportRepo, ReportEngine reportEngine,
                                EmailService emailService) {
    this.executor = executor;
    this.reportRepo = reportRepo;
    this.reportEngine = reportEngine;
    this.emailService = emailService;
  }

  public void scheduleReport(String reportName, String cronSchedule, String... emails) {
    if (reportName == null) {
      throw new IllegalArgumentException("ReportName cannot be null");
    }
    if (cronSchedule == null) {
      throw new IllegalArgumentException("Cron schedule cannot be null");
    }
    if (emails.length == 0) {
      throw new IllegalArgumentException("Report recepient emails are missing");
    }
    CronTrigger schedule = new CronTrigger(cronSchedule);

    Runnable reportTask = () -> {
        Optional<Report> reportOpt = reportRepo.findById(reportName);
        if (!reportOpt.isPresent()) {
          LOG.warn("Report {} does not exists, schedule report failed", reportName);
          return;
        }
        Report report = reportOpt.get();
      String html;
      try {
        html = reportEngine.runReport(report.getDefinition());
      } catch (ScriptException | ReportDefinitionException e) {
        LOG.warn("Scheduled report {} failed to run", reportName, e);
        return;
      }
      try {
        emailService.sendHtml(report.getReportName(), html, emails);
      } catch (MessagingException e) {
        LOG.warn("Scheduled report {} failed to email", reportName, e);
        return;
      }
      LOG.info("Report {}, executed on schedule and emailed successfully", reportName);
    };
    executor.schedule(reportTask, schedule);
  }
}
