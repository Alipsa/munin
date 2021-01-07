package se.alipsa.munin.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.model.web.ReportGroupInfo;

import java.util.List;
import java.util.Map;

@Repository
public interface ReportRepo extends CrudRepository<Report, String> {

  @Query("select distinct r.reportGroup from Report r")
  List<String> getReportGroups();

  @Query("select new se.alipsa.munin.model.web.ReportGroupInfo(r.reportGroup, count(r)) from Report r group by r.reportGroup")
  List<ReportGroupInfo> getGroupInfo();

  List<Report> findByReportGroupOrderByReportName(String reportGroup);
}
