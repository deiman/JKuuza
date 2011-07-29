package com.github.mefi.jkuuza.model;

import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Marek Pilecky
 */
public class Product extends CouchDbDocument {

	private String url;

	private String name;

	private String description;

	private String type;

	private String producer;

	private String price;

	private String priceDPH;

	private Map<String, String> params;

	public Product() {
		params = new HashMap<String, String>();
	}

	public Product(String name, String description, String price, String priceDPH, Map<String, String> params) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.priceDPH = priceDPH;
		this.params = params;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPriceDPH() {
		return priceDPH;
	}

	public void setPriceDPH(String priceDPH) {
		this.priceDPH = priceDPH;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
