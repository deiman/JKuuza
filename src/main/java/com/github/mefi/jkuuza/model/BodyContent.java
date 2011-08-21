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
	private String docType;
	private String url;
	private String date;
	private String bodyHtml;
	private String bodyText;
	private String hash;
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public BodyContent() {
		this.docType = "bodyContent";
	}

	public BodyContent(String url, String bodyHtml, String bodyText) {

		this.docType = "bodyContent";
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

	protected static String createId(String url, String hash) {
		return hash + "@" + url;
	}
}
