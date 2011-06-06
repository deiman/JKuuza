/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.mefi.jkuuza.model;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

/**
 *
 * @author Marek Pilecky
 */
public class BodyContentRepository extends CouchDbRepositorySupport<BodyContent> {

	public BodyContentRepository(CouchDbConnector connector) {
		super(BodyContent.class, connector);
		initStandardDesignDocument();
	}

	@GenerateView
	public List<BodyContent> findByHash(String hash) {
                return queryView("by_hash", hash);
        }


}
