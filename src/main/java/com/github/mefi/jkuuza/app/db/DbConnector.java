package com.github.mefi.jkuuza.app.db;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author Marek Pilecky
 */
public class DbConnector {

	private CouchDbConnector connector;	

	public CouchDbConnector getConnection() {
		if (connector == null) {
			init();
		}
		return connector;
	}

	private void init() {
		HttpClient httpClient = new StdHttpClient.Builder().build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		// if the second parameter is true, the database will be created if it doesn't exists
		this.connector = dbInstance.createConnector("jkuuza_test_database", true);
	}





	
}
