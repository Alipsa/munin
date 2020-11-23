package se.alipsa.munin.model.web;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.parser.CronParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import se.alipsa.munin.model.ReportSchedule;

@Component
public class ReportScheduleWebFactory {

  private final CronParser cronParser;
  CronDescriptor descriptor = CronDescriptor.instance();

  @Autowired
  public ReportScheduleWebFactory(@Qualifier("springCronParser") CronParser cronParser) {
    this.cronParser = cronParser;
  }

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
