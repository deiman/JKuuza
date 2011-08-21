package com.github.mefi.jkuuza.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Holds functions encapsulating JSoup functions, to determining if document contains or not some html structures.
 * Annotated functions have some additional informations, which can be used in gui
 *
 * @author Marek Pilecky
 */
public class ContentAnalyzer extends ContentHelper {

	public ContentAnalyzer() {
	}
	
	public ContentAnalyzer(Document doc) {
		this.doc = doc;
	}

	@MethodInfo(description="Obsahuje dokument třídu?", parameters="název_třídy")
	public boolean docHasClass(String className) {
		return !doc.getElementsByClass(className).isEmpty();
	}

	@MethodInfo(description="Obsahuje dokument id?", parameters="název_id")
	public boolean docHasId(String id) {
		return (doc.getElementById(id) != null);
	}

	@MethodInfo(description="Existuje v dokumentu zadaný tag?", parameters="název_tagu")
	public boolean docHasTag(String tagName) {
		return !doc.getElementsByTag(tagName).isEmpty();
	}

	@MethodInfo(description="Má tag uvedenou hodnotu?", parameters="název_tagu, hodnota")
	public boolean tagHasValue(String tagName, String value) {
		Elements elements = doc.getElementsByTag(tagName);
		for (Element element : elements) {
			if (element.ownText().equals(value)) {
				return true;
			}			
		}
		return false;
	}

	@MethodInfo(description="Má atribut tagu uvedenou hodnotu?", parameters="název_tagu, atribut, hodnota")
	public boolean tagHasAttributeWithValue(String tagName, String attribute, String attributeValue) {
		return doc.getElementsByTag(tagName).attr(attribute).equals(attributeValue);
	}


}
