package se.alipsa.munin.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.controller.ReportNotFoundException;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.model.web.ReportGroupInfo;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Report entities.
 * Provides methods to retrieve report groups, group info, and reports by group.
 */
@Repository
public interface ReportRepo extends CrudRepository<Report, String> {

  /**
   * Retrieves a list of distinct report groups from the reports.
   *
   * @return a list of unique report group names
   */
  @Query("select distinct r.reportGroup from Report r")
  List<String> getReportGroups();

  /**
   * Retrieves information about report groups, including the count of reports in each group.
   *
   * @return a list of ReportGroupInfo objects containing group names and their report counts
   */
  @Query("select new se.alipsa.munin.model.web.ReportGroupInfo(r.reportGroup, count(r)) from Report r group by r.reportGroup")
  List<ReportGroupInfo> getGroupInfo();

  /**
   * Finds reports by their report group and orders them by report name.
   *
   * @param reportGroup the name of the report group
   * @return a list of reports belonging to the specified group, ordered by report name
   */
  List<Report> findByReportGroupOrderByReportName(String reportGroup);

  /**
   * Loads a report by its name. If the report is not found, a ReportNotFoundException is thrown.
   *
   * @param name the name of the report to load
   * @return the Report object with the specified name
   * @throws ReportNotFoundException if no report with the given name exists
   */
  default Report loadReport(String name) throws ReportNotFoundException {
    Optional<Report> dbReport = findById(name);
    if (!dbReport.isPresent()) {
      throw new ReportNotFoundException("There is no report with the name " + name);
    }
    return dbReport.get();
  }

  /**
   * Retrieves a list of report names that are of type JOURNO.
   *
   * @return a list of report names with the JOURNO type
   */
  @Query("select r.reportName from Report r where r.reportType = se.alipsa.munin.model.ReportType.JOURNO")
  List<String> getJournoReports();
}
