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
