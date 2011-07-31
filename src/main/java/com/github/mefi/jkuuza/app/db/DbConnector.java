package com.github.mefi.jkuuza.app.db;

import com.github.mefi.jkuuza.data.ConfigLoader;
import java.io.IOException;
import java.util.Properties;
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

	public DbConnector(ConfigLoader configLoader) throws IOException, CouchDbConnectionException {

		Properties properties = configLoader.load();

		String host = !properties.getProperty("db_host").isEmpty() ? properties.getProperty("db_host") : DefaultDbParams.HOST.toString();
		Integer port = !properties.getProperty("db_port").isEmpty() ? Integer.parseInt(properties.getProperty("db_port")) : Integer.parseInt(DefaultDbParams.PORT.toString());
		String database = !properties.getProperty("db_database").isEmpty() ? properties.getProperty("db_database") : DefaultDbParams.DATABASE.toString();
		String username = !properties.getProperty("db_username").isEmpty() ? properties.getProperty("db_username") : DefaultDbParams.USERNAME.toString();
		String password = !properties.getProperty("db_password").isEmpty() ? properties.getProperty("db_password") : DefaultDbParams.PASSWORD.toString();

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

	public DbConnector(String host, Integer port, String dbName) throws CouchDbConnectionException {

		try {
			HttpClient httpClient = new StdHttpClient.Builder()
						.host(host)
						.port(port)
						.build();
			init(httpClient, dbName);
		} catch (Exception e) {
			throw new CouchDbConnectionException();
		}
	}

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
