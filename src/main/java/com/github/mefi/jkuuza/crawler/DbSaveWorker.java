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
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.Page;
import com.github.mefi.jkuuza.model.CrawledPageController;
import com.github.mefi.jkuuza.parser.ContentExtractor;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
				try {
					controller.save(page, bodyContent);
					CrawlerConsole.print("[crawled] - " + query.getOriginalURL().toString() + "     [" + query.getStatus() + "]");
				} catch (SocketTimeoutException ex) {
					CrawlerConsole.print("[error] - " + query.getOriginalURL().toString() + "     [DB RESPONSE ERROR]");
				}
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
			if (charset == null) {
				charset = "";
			}

			html = new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			// try it with default charset
			html = new String(bytes);
		}

		return html;
	}
}
