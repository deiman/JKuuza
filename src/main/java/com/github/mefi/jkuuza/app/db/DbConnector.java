package com.github.mefi.jkuuza.app.db;

import java.io.IOException;
import java.util.prefs.Preferences;
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

	/**
	 * Use default values to create connection to db
	 *
	 * @throws CouchDbConnectionException
	 */
	public DbConnector() throws CouchDbConnectionException {

		String host = DefaultDbParams.HOST.toString();
		Integer port = Integer.parseInt(DefaultDbParams.PORT.toString());
		String database = DefaultDbParams.DATABASE.toString();
		String username = DefaultDbParams.USERNAME.toString();
		String password = DefaultDbParams.PASSWORD.toString();

		try {
			HttpClient httpClient = null;
			httpClient = new StdHttpClient.Builder()
						.host(host)
						.port(port)
						.username(username)
						.password(password)
						.build();
			init(httpClient, database);
		} catch (Exception e) {
			throw new CouchDbConnectionException();
		}
	}

	/**
	 * Load connection values from properties and create connection to db
	 *
	 * @param configLoader
	 * @throws IOException
	 * @throws CouchDbConnectionException
	 */
	public DbConnector(Preferences preferences) throws CouchDbConnectionException {

		String host = preferences.get("db_host", DefaultDbParams.HOST.toString());
		Integer port = Integer.parseInt(preferences.get("db_port", DefaultDbParams.PORT.toString()));
		String database = preferences.get("db_database", DefaultDbParams.DATABASE.toString());
		String username = preferences.get("db_username", DefaultDbParams.USERNAME.toString());
		String password = preferences.get("db_password", DefaultDbParams.PASSWORD.toString());

		try {
			HttpClient httpClient = null;
			httpClient = new StdHttpClient.Builder()
						.host(host)
						.port(port)
						.username(username)
						.password(password)
						.build();
			init(httpClient, database);
		} catch (Exception e) {
			throw new CouchDbConnectionException();
		}
	}

	/**
	 * Use host, port and database to create connection to db
	 *
	 * @param host
	 * @param port
	 * @param database
	 * @throws CouchDbConnectionException
	 */
	public DbConnector(String host, Integer port, String database) throws CouchDbConnectionException {

		try {
			HttpClient httpClient = new StdHttpClient.Builder()
						.host(host)
						.port(port)
						.build();
			init(httpClient, database);
		} catch (Exception e) {
			throw new CouchDbConnectionException();
		}
	}

	/**
	 * Use host, port, database, username and password to create connection to db
	 *
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param dbName
	 * @throws CouchDbConnectionException
	 */
	public DbConnector(String host, Integer port, String username, String password, String dbName) throws CouchDbConnectionException {
		try {
			HttpClient httpClient = new StdHttpClient.Builder()
					.host(host)
					.port(port)
					.username(username)
					.password(password)
					.build();
			init(httpClient, dbName);
		} catch (Exception e) {
			throw new CouchDbConnectionException();
		}
	}

	/**
	 * Get connection to db
	 *
	 * @return connection
	 */
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
