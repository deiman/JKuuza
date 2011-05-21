package com.github.mefi.jkuuza.crawler;

import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.niocchi.core.Crawler;
import org.niocchi.core.Worker;
import org.niocchi.core.query.Query;
import com.github.mefi.jkuuza.parser.LinksExtractor;

/**
 *
 * @author Marek Pilecky
 */
public class DbSaveWorker extends Worker {

	Crawler crawler = null;
	ExpandableURLPool pool = null;

	public DbSaveWorker(Crawler crawler, ExpandableURLPool pool) {
		super(crawler);
		this.crawler = crawler;
		this.pool = pool;

	}

	/**
	 * Saves crawled content into db
	 * @param query
	 */
	public void processResource(Query query) {

		byte[] bytes = query.getResource().getBytes();
		String htmlContent = new String(bytes);

		String host = query.getHost();
		String hostUrl = "http://" + host;
		Document doc = Jsoup.parse(htmlContent, hostUrl);
		
		LinksExtractor extractor = new LinksExtractor(doc);
		Set<String> links = extractor.getInternalLinks(host);

		for (String link : links) {
			pool.addURL(link);
		}

		//TODO: save content to db
	}
}
