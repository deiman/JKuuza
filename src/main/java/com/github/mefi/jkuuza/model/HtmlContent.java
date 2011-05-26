package com.github.mefi.jkuuza.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Marek Pilecky
 */
public class HtmlContent extends CouchDbDocument {

	private String date;
	private String content;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public HtmlContent(String date, String content) {
		this.date = date;
		this.content = content;
	}

	public HtmlContent(String content) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

		this.date = dateFormat.format(calendar.getTime());
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
