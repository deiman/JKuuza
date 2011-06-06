package com.github.mefi.jkuuza.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Marek Pilecky
 */
public class BodyContent extends CouchDbDocument {

	@JsonProperty("_id")
	private String id;
	
	private String url;
	private String date;
	private String bodyHtml;
	private String bodyText;
	private String hash;
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public BodyContent() {
	}

	

	public BodyContent(String url, String bodyHtml, String bodyText) {
		
		this.hash = DigestUtils.md5Hex(bodyHtml);
		this.url = url;
		this.bodyHtml = bodyHtml;
		this.bodyText = bodyText;
		this.date = getFormatedDateTime();
		
		this.id = createId(url, hash);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getBodyHtml() {
		return bodyHtml;
	}

	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
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

	protected static String createId(String url, String hash) {
		return hash + "@" + url;
	}
}
