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
