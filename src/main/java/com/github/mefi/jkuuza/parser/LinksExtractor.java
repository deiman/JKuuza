package com.github.mefi.jkuuza.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Marek Pilecky
 */
public class LinksExtractor {

	private Document doc;

	/**
	 * Needs non empty Jsoup Document instance
	 * 
	 * @param doc Jsoup Document
	 */
	public LinksExtractor(Document doc) {
		this.doc = doc;
	}

	/**
	 * Extracts all links from document and returns links from specified domain
	 *
	 * @param host String: "foo.example.com"
	 * @return set with with links pointing to the host.
	 */
	public Set<String> getInternalLinks(String host) {

		Elements allLinks = doc.select("a[href]");
		Set<String> internalLinks = new HashSet<String>();

		Pattern pattern = Pattern.compile("^(http|https|ftp)://(www.)?" + host + "*");

		if (!host.isEmpty()) {
			host = host.replace("www.", "");
		}

		for (Element link : allLinks) {

			String linkUrl = link.attr("abs:href").toString();
			Matcher matcher = pattern.matcher(linkUrl);

			if (!link.attr("abs:href").isEmpty() && matcher.find()) {

				// remove anchor
				if (linkUrl.contains("#")) {
					linkUrl = linkUrl.substring(0, linkUrl.indexOf("#"));
				}

				if (internalLinks.add(linkUrl)) {
				}

			}
		}
		return internalLinks;
	}
}
