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
