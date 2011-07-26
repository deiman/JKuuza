package com.github.mefi.jkuuza.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
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
	public HashMap<String, Integer> getCountOfRecordsPerHost() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		ViewQuery query = new ViewQuery()
					.designDocId("_design/Page")
					.viewName("count_of_records_per_host")
					.reduce(true)
					.group(true);
		ViewResult r = db.queryView(query);
		
		for (ViewResult.Row row : r.getRows()) {
			map.put(row.getKey(), Integer.parseInt(row.getValue()));
		}
		return map;
	}
}
