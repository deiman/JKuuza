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

	public DbConnector() {
		HttpClient httpClient = new StdHttpClient.Builder().build();
		String dbName = "jkuuza";
		init(httpClient, dbName);
	}

	public DbConnector(String host, Integer port, String dbName) {
		HttpClient httpClient = new StdHttpClient.Builder()
					.host(host)
					.port(port)
					.build();
		init(httpClient, dbName);
	}

	public DbConnector(String host, Integer port, String username, String password, String dbName) {
		HttpClient httpClient = new StdHttpClient.Builder()
					.host(host)
					.port(port)
					.username(username)
					.password(password)
					.build();
		init(httpClient, dbName);
	}	

	public CouchDbConnector getConnection() {
		return connector;
	}

	private void init(HttpClient httpClient, String dbName) {
		HttpClient client = httpClient;
		CouchDbInstance dbInstance = new StdCouchDbInstance(client);
		// if the second parameter is true, the database will be created if it doesn't exists
		this.connector = dbInstance.createConnector(dbName, true);
		System.setProperty("org.ektorp.support.AutoUpdateViewOnChange", "true");
	}





	
}
