package se.alipsa.renjin.webreports.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.renjin.webreports.model.Report;

@Repository
public interface ReportRepo extends CrudRepository<Report, Long> {

}
