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

	int resourcesCount = 30; // number of url that can be crawled simultaneously
	String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24";

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
		CrawlerConsole.printNewLine();

		// start crawler
		crawler.run(urlPool);

		// wait for workers to finish
		worker.join();
		CrawlerConsole.printNewLine();
		CrawlerConsole.print("Crawler finished.", true);

		CrawlerConsole.print("Doba crawlování: " + this.crawler.select_total_time/60 + "vteřin");
		CrawlerConsole.print(this.crawler.processed_count + " URL processed");
		CrawlerConsole.print(this.crawler.status_200 + " with status 200");
		CrawlerConsole.print(this.crawler.redirected_count + " redirections");
		CrawlerConsole.print(this.crawler.status_other + " other status");
		CrawlerConsole.print(this.crawler.incomplete_count + " incomplete");
	}

	public void execute(List list) throws Exception {
		this.crawl(list);
	}
}
