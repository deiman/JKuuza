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

package com.github.mefi.jkuuza.parser;

import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

/**
 * Holds functions encapsulating JSoup functions, to extracting values from html elements
 * Annotated functions have some additional informations, which can be used in gui
 *
 * @author Marek Pilecky
 */
public class ContentExtractor extends ContentHelper {

	public ContentExtractor() {
	}

	/**
	 * Needs non empty Jsoup Document instance
	 *
	 * @param doc Jsoup Document
	 */
	public ContentExtractor(Document doc) {
		this.doc = doc;
	}

	/**
	 * Gets title from page
	 *
	 * @return title of page
	 */
	public String getTitle() {
		String title = doc.title();

		return title;
	}

	/**
	 * Returns description from meta tag
	 *
	 * @return description value of content attribute
	 */
	public String getMetaDescription() {
		String description = doc.select("meta[name=description]").first().attr("content");

		return description;
	}

	/**
	 * Checks, if header of page contains meta tag with description
	 *
	 * @return true if page contains description
	 */
	public boolean hasMetaDescription() {
		Elements elements = doc.head().select("meta[name=description]");

		if (elements.isEmpty() || !elements.first().hasAttr("content")) {
			return false;
		}
		return true;
	}

	/**
	 * Returns keywords from meta tag
	 *
	 * @return keywords value of content attribute
	 */
	public String getMetaKeywords() {
		String keywords = doc.select("meta[name=keywords]").first().attr("content");

		return keywords;
	}

	/**
	 * Checks, if header of page contains meta tag with keywords
	 *
	 * @return true if page contains keywords
	 */
	public boolean hasMetaKeywords() {
		Elements elements = doc.head().select("meta[name=keywords]");

		if (elements.isEmpty() || !elements.first().hasAttr("content")) {
			return false;
		}
		return true;
	}

	/**
	 * Returns charset from meta tag
	 *
	 * @return charset value of content attribute
	 */
	public String getMetaCharset() {
		String charset = "";
		charset = doc.select("meta[http-equiv=content-type]").first().attr("content");

		charset = charset.replace("text/html", "");
		charset = charset.replace("TEXT/HTML", "");
		charset = charset.replace("charset", "");
		charset = charset.replace("CHARSET", "");
		charset = charset.replace("=", "");
		charset = charset.replace(";", "");
		charset = charset.replace(" ", "");

		return charset;
	}

	/**
	 * Checks, if header of page contains meta tag with charset
	 *
	 * @return true if page contains charset value
	 */
	public boolean hasMetaCharset() {
		Elements elements = doc.head().select("meta[http-equiv=content-type]");

		if (elements.isEmpty() || !elements.first().hasAttr("content")) {
			return false;
		}
		return true;
	}

	
	public String getValue(String selector) {
		if (selector.equals("")) {
			return "";
		}
		return doc.select(selector).text();
	}

	public ArrayList<String> getValuesOf(String selector) {
		Elements elements = doc.select(selector);
		ArrayList<String> list = new ArrayList();
		for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
			list.add(it.next().text());
		}
		return list;
	}

	
}
