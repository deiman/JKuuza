package com.github.mefi.jkuuza.model;

import java.util.HashMap;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.*;

/**
 *
 * @author Marek Pilecky
 */
public class PageRepository extends CouchDbRepositorySupport<Page> {

	public PageRepository(CouchDbConnector db) {
		super(Page.class, db);
		initStandardDesignDocument();
	}

	@GenerateView
	public List<Page> findByHost(String host) {
		return queryView("by_host", host);
	}

	/**
	 * Returns map where key is the host and value is count of records with this host
	 *
	 * @return map: key = host; value = count of records
	 */

	@View(name = "count_of_records_per_host", map = "function(doc) { if(doc.docType == \"page\" && doc.host) {emit(doc.host, 1)}}", reduce = "function(keys, values) {return sum(values)}")
	public HashMap<String, String> getCountOfRecordsPerHost() {
		HashMap<String, String> map = new HashMap<String, String>();

		ViewResult r = db.queryView(createQuery("count_of_records_per_host"));
		for (ViewResult.Row row : r) {
			map.put(row.getKey(), row.getValue());
		}
		return map;
	}
}
