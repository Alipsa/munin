package se.alipsa.munin.model.web;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.parser.CronParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import se.alipsa.munin.model.ReportSchedule;

/**
 * Factory class for creating ReportScheduleWeb objects from ReportSchedule objects.
 */
@Component
public class ReportScheduleWebFactory {

  private final CronParser cronParser;
  CronDescriptor descriptor = CronDescriptor.instance();

  /**
   * Constructor with autowired dependencies.
   *
   * @param cronParser the CronParser for parsing cron expressions
   */
  @Autowired
  public ReportScheduleWebFactory(@Qualifier("springCronParser") CronParser cronParser) {
    this.cronParser = cronParser;
  }

  /**
   * Creates a ReportScheduleWeb object from a ReportSchedule object, adding a human-readable description of the cron expression.
   *
   * @param schedule the ReportSchedule object
   * @return a ReportScheduleWeb object with additional cron description
   */
  public ReportScheduleWeb create(ReportSchedule schedule) {
    return new ReportScheduleWeb(
        schedule.getId(),
        schedule.getReportName(),
        schedule.getCron(),
        schedule.getEmails(),
        descriptor.describe(cronParser.parse(schedule.getCron()))
    );
  }
}
