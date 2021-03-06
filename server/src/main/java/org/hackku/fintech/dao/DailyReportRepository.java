package org.hackku.fintech.dao;

import java.util.List;

import org.hackku.fintech.domains.Business;
import org.hackku.fintech.domains.DailyReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyReportRepository extends CrudRepository<DailyReport, Long>{

	public List<DailyReport> findByBusinessOrderByCreatedDesc(Business business);
	
}
