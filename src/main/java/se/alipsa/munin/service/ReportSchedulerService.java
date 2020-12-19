package se.alipsa.munin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.model.ReportSchedule;
import se.alipsa.munin.repo.ReportRepo;
import se.alipsa.munin.repo.ReportScheduleRepo;

import javax.mail.MessagingException;
import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
public class ReportSchedulerService implements
    ApplicationListener<ContextRefreshedEvent> {

  private final TaskScheduler executor;
  private final ReportRepo reportRepo;
  private final ReportScheduleRepo reportScheduleRepo;
  private final ReportEngine reportEngine;
  private final EmailService emailService;
  private final Map<Long, ScheduledFuture<?>> currentSchedules = new HashMap<>();

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

  private void scheduleReport(ReportSchedule reportSchedule) {
    String[] reportRecipients;
    if (reportSchedule.getEmails().indexOf(';') > 0){
      reportRecipients = reportSchedule.getEmails().split(";");
    } else {
      reportRecipients = new String[]{reportSchedule.getEmails()};
    }
    String reportName = reportSchedule.getReportName();
    String cronSchedule = reportSchedule.getCron();
    String[] emails = reportRecipients;
    if (reportName == null) {
      throw new IllegalArgumentException("ReportName cannot be null");
    }
    if (cronSchedule == null) {
      throw new IllegalArgumentException("Cron schedule cannot be null");
    }
    if (emails.length == 0) {
      throw new IllegalArgumentException("Report recipient emails are missing");
    }
    CronTrigger cronTrigger = new CronTrigger(cronSchedule);

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
    ScheduledFuture<?> future = executor.schedule(reportTask, cronTrigger);
    currentSchedules.put(reportSchedule.getId(), future);
  }

  public void addReportSchedule(ReportSchedule reportSchedule) {
    ReportSchedule dbSchedule = reportScheduleRepo.save(reportSchedule);
    scheduleReport(dbSchedule);
  }

  @Transactional
  public String updateReportSchedule(Long id, ReportSchedule schedule) {
    Optional<ReportSchedule> reportScheduleOpt = reportScheduleRepo.findById(id);
    if (!reportScheduleOpt.isPresent()) {
      throw new IllegalArgumentException("There is no report schedule for id " + id);
    }
    ReportSchedule reportSchedule = reportScheduleOpt.get();

    // remove the current schedule
    stopAndRemove(id);

    // Report name will be null since we disable the combobox so we should not change it
    reportSchedule.setCron(schedule.getCron());
    reportSchedule.setEmails(schedule.getEmails());

    scheduleReport(reportSchedule);
    //reportScheduleRepo.save(reportSchedule); we are in a transaction so should not be needed
    return reportSchedule.getReportName();
  }

  private void stopAndRemove(Long id) {
    currentSchedules.remove(id).cancel(true);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    LOG.info("Activating stored schedules");
    reportScheduleRepo.findAll().forEach(this::scheduleReport);
  }


  public ReportSchedule deleteReportSchedule(Long id) {
    Optional<ReportSchedule> reportScheduleOpt = reportScheduleRepo.findById(id);
    if (!reportScheduleOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Report Schedule with the id " + id);
    }
    ReportSchedule schedule = reportScheduleOpt.get();
    stopAndRemove(id);
    reportScheduleRepo.delete(schedule);
    return schedule;
  }
}
