package se.alipsa.munin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.model.ReportSchedule;
import se.alipsa.munin.repo.ReportRepo;
import se.alipsa.munin.repo.ReportScheduleRepo;

import javax.mail.MessagingException;
import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ReportSchedulerService implements
    ApplicationListener<ContextRefreshedEvent> {

  private final TaskScheduler executor;
  private final ReportRepo reportRepo;
  private final ReportScheduleRepo reportScheduleRepo;
  private final ReportEngine reportEngine;
  private final EmailService emailService;

  private final static Logger LOG = LoggerFactory.getLogger(ReportSchedulerService.class);

  @Autowired
  public ReportSchedulerService(TaskScheduler executor, ReportRepo reportRepo, ReportScheduleRepo reportScheduleRepo, ReportEngine reportEngine,
                                EmailService emailService) {
    this.executor = executor;
    this.reportRepo = reportRepo;
    this.reportScheduleRepo = reportScheduleRepo;
    this.reportEngine = reportEngine;
    this.emailService = emailService;
  }

  private void scheduleReport(String reportName, String cronSchedule, String... emails) {
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
      LocalDateTime dateTime = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String html;
      try {
        html = reportEngine.runReport(report.getDefinition());
      } catch (ScriptException | ReportDefinitionException e) {
        LOG.warn("Scheduled report {} failed to run", reportName, e);
        return;
      }
      try {
        emailService.sendHtml(report.getReportName() + " executed on " + formatter.format(dateTime), html, emails);
      } catch (MessagingException e) {
        LOG.warn("Scheduled report {} failed to email", reportName, e);
        return;
      }
      LOG.info("Report {}, executed on schedule and emailed successfully", reportName);
    };
    executor.schedule(reportTask, schedule);
  }

  private void scheduleReport(ReportSchedule schedule) {
    String[] reportRecipients;
    if (schedule.getEmails().indexOf(';') > 0){
      reportRecipients = schedule.getEmails().split(";");
    } else {
      reportRecipients = new String[]{schedule.getEmails()};
    }
    scheduleReport(schedule.getReportName(), schedule.getCron(), reportRecipients);
  }

  public void addReportSchedule(ReportSchedule schedule) {
    scheduleReport(schedule);
    reportScheduleRepo.save(schedule);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    LOG.info("Activating stored schedules");
    reportScheduleRepo.findAll().forEach(this::scheduleReport);
  }
}
