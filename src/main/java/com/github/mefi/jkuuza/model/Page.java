package com.github.mefi.jkuuza.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Marek Pilecky
 */
public class Page extends CouchDbDocument {
	private String docType;

	private String host;

	private String url;

	private String title;

	private String description;

	private String keywords;

	private String charset;

	private String dateCrawled;
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	
	public Page() {
		docType = "page";
	}	

	public Page(String url, String host) {
		docType = "page";
		this.host = host;
		this.url = url;
		this.dateCrawled = getFormatedDateTime();
		setId(url);
	}
	
	//@DocumentReferences(backReference = "url", fetch = FetchType.EAGER, descendingSortOrder = true, orderBy = "date")
	private Set<BodyContent> contents;


	public Set<BodyContent> getContents() {
		return contents;
	}

	public void setContents(Set<BodyContent> contents) {
		this.contents = contents;
	}

	public void addContent(BodyContent content) {
		if (getContents() == null) {
			contents = new TreeSet<BodyContent>();
		}
		content.setUrl(this.getUrl());
		contents.add(content);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDateCrawled() {
		return dateCrawled;
	}

	public void setDateCrawled(String dateCrawled) {
		this.dateCrawled = dateCrawled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets current date and time and formates it
	 *
	 * @return String with formated date and time
	 */
	private static String getFormatedDateTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		return dateFormat.format(calendar.getTime());
	}

}
