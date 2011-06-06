package com.github.mefi.jkuuza.parser;

import java.net.URI;
import java.net.URISyntaxException;
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

		host = canonizeHost(host);

		for (Element link : allLinks) {
			String linkUrl = createLinkUrl(link);

			if (isInternal(linkUrl, host)) {
				internalLinks.add(linkUrl);
			}
		}
		return internalLinks;
	}

	/**
	 * Creates normalized url from link in org.jsoup.nodes.Element
	 *
	 * @param link Element
	 * @return String with url
	 */
	public String createLinkUrl(Element link) {

		String linkUrl = link.attr("abs:href").toString();
		return normalizeUrl(linkUrl);
		
	}

	/**
	 * Check if link points back to the domain specified by host
	 *
	 * @param linkUrl
	 * @param host
	 * @return true if host is part od link url
	 */
	public boolean isInternal(String linkUrl, String host) {
		Pattern pattern = Pattern.compile("^(http|https|ftp)://(www.)?[a-zA-Z0-9.]*" + host + "*");
		Matcher matcher = pattern.matcher(linkUrl);

		if (!linkUrl.isEmpty() && matcher.find()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Transforms String host to required form
	 *
	 * @param host
	 * @return host in form example.com; foo.example.com; ...
	 */
	protected String canonizeHost(String host) {
		host = host.trim();
		host = host.replace("https://", "");
		host = host.replace("http://", "");
		host = host.replace("www.", "");

		return host;
	}

	/**
	 * Removes php session from url
	 *
	 * @param host
	 * @return host in form example.com; foo.example.com; ...
	 */
	protected String removePhpsessid(String string) {
		if (string.contains("PHPSESSID")) {
			Pattern pattern = Pattern.compile("PHPSESSID=[a-z0-9]{32}[&]?");
			Matcher matcher = pattern.matcher(string);
			String output = matcher.replaceAll("");

			if (output.endsWith("?")) {
				output = output.substring(0, output.lastIndexOf("?"));
			}
			if (output.endsWith("&")) {
				output = output.substring(0, output.lastIndexOf("&"));
			}

			return output;
		}
		return string;

	}

	public String normalizeUrl(String url) {

		URI uri = null;
		
		// remove anchor
		if (url.contains("#")) {
			url = url.substring(0, url.indexOf("#"));
		}
		
		// remove /../
		if (url.contains("/../")) {
			url = url.replace("/../", "/");
		}

		// remove PHPSESSID
		url = removePhpsessid(url);

		// remove . 
		// remove ..
		// remove :
 		try {
			uri = new URI(url);
			uri = uri.normalize();

		} catch (URISyntaxException ex) {
			return "";
		}
		return uri.toString();

	}

	/**
	 * Returns Document
	 *
	 * @return Doc
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * Sets document
	 *
	 * @param doc Document
	 */
	public void setDoc(Document doc) {
		this.doc = doc;
	}
}
