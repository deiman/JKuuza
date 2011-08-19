package com.github.mefi.jkuuza.model;

import org.ektorp.CouchDbConnector;

/**
 *
 * @author Marek Pilecky
 */
public class ProductController {

	private CouchDbConnector connector;
	private ProductRepository repository;

	public ProductController(CouchDbConnector connector) {
		this.connector = connector;
		this.repository = new ProductRepository(connector);
	}

	public void save(Product product) {
		Product p = repository.findByUrl(product.getUrl());
		if (p == null) {
			repository.add(product);			
		} 
	}

}
