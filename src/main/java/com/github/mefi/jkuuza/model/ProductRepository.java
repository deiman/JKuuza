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

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;

/**
 *
 * @author Marek Pilecky
 */
public class ProductRepository extends CouchDbRepositorySupport<Product>{

	public ProductRepository(CouchDbConnector db) {
		super(Product.class, db);
		initStandardDesignDocument();
	}

	@View(name = "by_url", map = "function(doc) { if(doc && doc.docType == \"product\") { emit(doc.url, doc) }}")
	public Product findByUrl(String url) {
		ViewQuery query = new ViewQuery()
					.designDocId("_design/Product")
					.viewName("by_url")
					.key(url);

		if (db.queryView(query, Product.class).size() > 0) {
			return db.queryView(query, Product.class).get(0);
		}
		return null;
	}

	@View(name = "by_name", map = "function(doc) { if(doc && doc.docType == \"product\") { emit(doc.name, doc) }}")
	public Product findByName(String name) {
		ViewQuery query = new ViewQuery()
					.designDocId("_design/Product")
					.viewName("by_name")
					.key(name);

		if (db.queryView(query, Product.class).size() > 0) {
			return db.queryView(query, Product.class).get(0);
		}
		return null;
	}

	@View(name = "by_producer", map = "function(doc) { if(doc && doc.docType == \"product\") { emit(doc.producer, doc) }}")
	public Product findByProducer(String producer) {
		ViewQuery query = new ViewQuery()
					.designDocId("_design/Product")
					.viewName("by_producer")
					.key(producer);

		if (db.queryView(query, Product.class).size() > 0) {
			return db.queryView(query, Product.class).get(0);
		}
		return null;
	}


	@View(name = "by_extended_parameter_value", map = "function(doc) { if(doc && doc.docType == \"product\" && doc.params) { for (param in doc.params) { emit(doc.params[param], doc.name) }}}")
	public Product findByExtendedParameterValue(String value) {
		ViewQuery query = new ViewQuery()
					.designDocId("_design/Product")
					.viewName("by_extended_parameter_value")
					.key(value);

		if (db.queryView(query, Product.class).size() > 0) {
			return db.queryView(query, Product.class).get(0);
		}
		return null;
	}
}
