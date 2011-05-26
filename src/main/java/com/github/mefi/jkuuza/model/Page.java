package com.github.mefi.jkuuza.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Marek Pilecky
 */
public class Page {

	@JsonProperty("_id")
	private String url;
	@JsonProperty("_rev")
	private String revision;
	private String host;
	private Set<HtmlContent> contents;

	public Page() {
	}

	/**
	 * Creates Page CouchDbDocument
	 *
	 * @param url
	 * @param host
	 */
	public Page(String url, String host) {
		this.url = url;
		this.host = host;
		contents = new HashSet<HtmlContent>();
	}

	public Page(URL url, String host) {
		this.url = url.toString();
		this.host = host;
		contents = new HashSet<HtmlContent>();
	}

	public Set<HtmlContent> getContents() {
		return contents;
	}

	public void setContents(Set<HtmlContent> contents) {
		this.contents = contents;
	}

	public void addContent(HtmlContent content) {
		contents.add(content);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("_id")
	public String getUrl() {
		return url;
	}

	@JsonProperty("_id")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("_rev")
	public String getRevision() {
		return revision;
	}

	@JsonProperty("_rev")
	public void setRevision(String revision) {
		this.revision = revision;
	}
}
