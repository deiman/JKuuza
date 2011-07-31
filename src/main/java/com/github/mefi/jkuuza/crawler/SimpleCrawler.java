package com.github.mefi.jkuuza.crawler;

import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.crawler.gui.CrawlerConsole;
import java.io.IOException;
import java.util.List;
import org.niocchi.core.Crawler;
import org.niocchi.core.ResourceException;

import org.niocchi.core.URLPoolException;
import org.niocchi.core.Worker;

/**
 *
 * @author Marek Pilecky
 */
public class SimpleCrawler {

	int resourcesCount = 10; // number of url that can be crawled simultaneously
	int monitorPort = 6001;
	String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24";
	String seedFile = null;

	DbConnector connector;
	TimeoutURLPool urlPool;
	Crawler crawler;
	Worker worker;

	public SimpleCrawler(DbConnector connector) {
		this.connector = connector;
	}

	/**
	 * Creates crawler and sets its dependencies
	 * 
	 * @param list List of domains to crawle
	 * @throws IOException
	 */
	private void init(List list) throws IOException {

		// create the worker
		crawler = new Crawler(new HTMLResourceFactory(), resourcesCount);
		crawler.setUserAgent(userAgent);

		// create the url pool
		urlPool = new TimeoutURLPool(new ExpandableURLPool(list));

		// create the worker
		worker = new DbSaveWorker(crawler, urlPool, connector);
		// print info
		CrawlerConsole.print("Crawler initialized.", true);
	}

	
	public void crawl(List list) throws IOException, InterruptedException, ResourceException, URLPoolException {

		this.init(list);

		// start workers
		worker.start();
		CrawlerConsole.print("Crawler started.", true);

		// start crawler
		crawler.run(urlPool);

		// wait for workers to finish
		worker.join();
		CrawlerConsole.print("Crawler finished.", true);
	}

	public void execute(List list) throws Exception {
		this.crawl(list);
	}
}
