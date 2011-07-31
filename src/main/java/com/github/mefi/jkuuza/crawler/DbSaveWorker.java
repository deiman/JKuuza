package com.github.mefi.jkuuza.crawler;

import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.crawler.gui.CrawlerConsole;
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.Page;
import com.github.mefi.jkuuza.model.CrawledPageController;
import com.github.mefi.jkuuza.parser.ContentExtractor;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.niocchi.core.Crawler;
import org.niocchi.core.Worker;
import com.github.mefi.jkuuza.parser.LinksExtractor;
import org.niocchi.core.MemoryResource;
import org.niocchi.core.Query;

/**
 *
 * @author Marek Pilecky
 */
public class DbSaveWorker extends Worker {

	TimeoutURLPool pool = null;
	private DbConnector connector;

	public DbSaveWorker(Crawler crawler, TimeoutURLPool pool, DbConnector connector) {
		super(crawler);
		this.pool = pool;
		this.connector = connector;

	}

	/**
	 * Saves crawled content into db
	 * @param query
	 */
	public void processResource(Query query) {
		CrawlerConsole.print("[crawled] - " + query.getOriginalURL().toString());


		if (query.getResource().getContentMimeSubType() != null) {
			if (query.getResource().getContentMimeSubType().contains("html")) {
				String html = getCrawledHtml(query);
				String url = query.getOriginalURL().toString();
				String host = query.getHost();

				if (url.split("/").length < 4) {
					//http://example.com <- no slash at the end
					url = url + "/";
				}
				String baseUrl = url.substring(0, url.lastIndexOf('/') + 1);

				Document doc = Jsoup.parse(html, baseUrl);
				LinksExtractor extractor = new LinksExtractor(doc);

				// extract links pointing back to the host and add them into url pool
				Set<String> links = extractor.getInternalLinks(host);
				for (String link : links) {					
					ExpandableURLPool expPool = (ExpandableURLPool) pool.getUrlPool();
					expPool.addURL(link);
				}

				ContentExtractor contentExtractor = new ContentExtractor(doc);

				Page page = new Page(query.getOriginalURL().toString(), extractor.canonizeHost(host));

				if (contentExtractor.hasMetaDescription()) {
					page.setDescription(contentExtractor.getMetaDescription());
				}
				if (contentExtractor.hasMetaKeywords()) {
					page.setKeywords(contentExtractor.getMetaKeywords());
				}
				if (contentExtractor.hasMetaCharset()) {
					page.setCharset(contentExtractor.getMetaCharset());
				}

				String bodyText = doc.body().text();
				String bodyHtml = doc.body().toString();

				BodyContent bodyContent = new BodyContent(page.getUrl(), bodyHtml, bodyText);

				CrawledPageController controller = new CrawledPageController(connector.getConnection());
				controller.save(page, bodyContent);
			} else {
				System.out.println(query.getResource().getContentMimeSubType());
			}
		}
	}

	/**
	 * Extracts html code from Query and returns it as a string.
	 * Function tries to determine which encoding is used and applies it.
	 * If it fails or encoding isn`t set, it uses default encoding.
	 *
	 * @param query
	 * @return html - String with html from webpage
	 */
	public String getCrawledHtml(Query query) {

		String charset = "";
		String html = null;

		MemoryResource resource = (MemoryResource) query.getResource();
		byte[] bytes = resource.getBytes();
		charset = query.getResource().getContentEncoding();

		if (charset == null || charset.equals("")) {
			String tempHtml = new String(bytes);
			Document doc = Jsoup.parse(tempHtml);
			ContentExtractor contentExtractor = new ContentExtractor(doc);
			// extract charset from meta
			if (contentExtractor.hasMetaCharset()) {
				charset = contentExtractor.getMetaCharset();
			} 
		} 

		try {
			html = new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			// try it with default charset
			html = new String(bytes);
		}

		return html;
	}
}
