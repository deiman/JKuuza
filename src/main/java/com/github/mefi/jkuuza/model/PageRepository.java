package com.github.mefi.jkuuza.model;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

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


	

}
