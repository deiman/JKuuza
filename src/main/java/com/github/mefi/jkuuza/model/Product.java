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

import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Marek Pilecky
 */
public class Product extends CouchDbDocument {
	private String docType;

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
		docType = "product";
	}

	public Product(String name, String description, String price, String priceDPH, Map<String, String> params) {
		docType = "product";
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

}
