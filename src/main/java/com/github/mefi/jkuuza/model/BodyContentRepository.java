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
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;

/**
 *
 * @author Marek Pilecky
 */
public class BodyContentRepository extends CouchDbRepositorySupport<BodyContent> {

	

	public BodyContentRepository(CouchDbConnector connector) {
		super(BodyContent.class, connector);
		initStandardDesignDocument();
	}

	/**
	 * Finds BodyContent document by url
	 *
	 * @param url
	 * @return List of BodyContent instances
	 */
	@View(name = "by_url", map = "function(doc) { if(doc && doc.docType == \"bodyContent\") { emit(doc.url, doc) }}")
	public BodyContent findByUrl(String url) {
		ViewQuery query = new ViewQuery()
					.designDocId("_design/BodyContent")
					.viewName("by_url")
					.key(url);

		return db.queryView(query, BodyContent.class).get(0);
	}

}
