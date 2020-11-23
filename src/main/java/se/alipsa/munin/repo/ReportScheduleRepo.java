package se.alipsa.munin.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.model.ReportSchedule;

@Repository
public interface ReportScheduleRepo extends CrudRepository<ReportSchedule, Long> {
}
