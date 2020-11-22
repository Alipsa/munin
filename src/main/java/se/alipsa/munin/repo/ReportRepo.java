package se.alipsa.munin.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.model.Report;

@Repository
public interface ReportRepo extends CrudRepository<Report, String> {

}
