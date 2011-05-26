package com.github.mefi.jkuuza.crawler;

import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.crawler.gui.CrawlerConsole;
import com.github.mefi.jkuuza.model.HtmlContent;
import com.github.mefi.jkuuza.model.Page;
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

		CrawlerConsole.print("[crawled] - " + query.getOriginalURL().toString());

		byte[] bytes = query.getResource().getBytes();
		String html = new String(bytes);

		String host = query.getHost();
		String hostUrl = "http://" + host;
		Document doc = Jsoup.parse(html, hostUrl);

		LinksExtractor extractor = new LinksExtractor(doc);
		Set<String> links = extractor.getInternalLinks(host);

		for (String link : links) {
			pool.addURL(link);
		}

		if (!html.isEmpty()) {
			Page page = new Page(query.getOriginalURL(), host);
			HtmlContent htmlContent = new HtmlContent(html);
			page.addContent(htmlContent);

			//just a test
			DbConnector conn = new DbConnector();
			conn.getConnector().create(page);
		}
	}
}
