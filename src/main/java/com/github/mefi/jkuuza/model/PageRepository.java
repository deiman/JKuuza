/*
 *   Copyright 2011 Marek Pilecky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mefi.jkuuza.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


	@View(name = "find_urls_by_host", map = "function(doc) { if(doc.docType == \"page\") { emit(doc.host, doc.url) }}")
	public List<String> findUrlsByHost(String host) {
		List<String> list = new ArrayList<String>();

		ViewQuery query = new ViewQuery()
					.designDocId("_design/Page")
					.viewName("find_urls_by_host")
					.key(host);
		ViewResult r = db.queryView(query);

		for (ViewResult.Row row : r.getRows()) {
			list.add(row.getValue());
		}
		return list;
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
