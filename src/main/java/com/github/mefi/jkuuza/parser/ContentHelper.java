package com.github.mefi.jkuuza.parser;

import org.jsoup.nodes.Document;

/**
 * @author Marek Pilecky
 */
public abstract class ContentHelper {

	protected Document doc;

	public Document getDocument() {
		return doc;
	}

	public void setDocument(Document doc) {
		this.doc = doc;
	}
}