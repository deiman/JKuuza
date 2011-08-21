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

package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.model.BasicProductProperties;
import com.github.mefi.jkuuza.model.Product;
import com.github.mefi.jkuuza.parser.ContentExtractor;
import java.util.ArrayList;
import java.util.Map;
import org.codehaus.jackson.sym.Name1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Marek Pilecky
 */
public class ExtractionResolver {

	private Rules rules;

	public ExtractionResolver(Rules rules) {
		this.rules = rules;
	}

	/**
	 * Apply all selectors from Rules and gets values from document. 
	 *
	 * @param doc
	 * @return Product 
	 */
	public Product resolve(Document doc) {

		Product product = new Product();

		Map<String, String> map = rules.getValues();

		ArrayList<String> paramValues = new ArrayList<String>();
		ArrayList<String> paramNames = new ArrayList<String>();

		for (Map.Entry<String, String> en : map.entrySet()) {
			String propertyName = en.getKey();
			String selector = en.getValue();

			ContentExtractor extractor = new ContentExtractor(doc);

			if (propertyName.equals(BasicProductProperties.PARAMETER_NAME.toString())) {
				paramNames = extractor.getValuesOf(selector);
			} else if (propertyName.equals(BasicProductProperties.PARAMETER_VALUE.toString())) {
				paramValues = extractor.getValuesOf(selector);
			} else {
				String name = propertyName;
				String value = "";
				try {
					value = extractor.getValue(selector);
				} catch (RuntimeException ex){
					ex.printStackTrace();
				}
				

				//TODO: refactore this
				if (name.equals(BasicProductProperties.NAME.toString())) {
					product.setName(value);
				}
				if (name.equals(BasicProductProperties.DESCRIPTION.toString())) {
					product.setDescription(value);
				}
				if (name.equals(BasicProductProperties.PRICE.toString())) {
					product.setPrice(value);
				}
				if (name.equals(BasicProductProperties.PRICE_DPH.toString())) {
					product.setPriceDPH(value);
				}
				if (name.equals(BasicProductProperties.PRODUCER.toString())) {
					product.setProducer(value);
				}
				if (name.equals(BasicProductProperties.TYPE.toString())) {
					product.setType(value);
				}
			}
		}

		for (int i = 0; i < paramNames.size() && i < paramValues.size(); i++) {
			product.getParams().put(paramNames.get(i), paramValues.get(i));
		}

		return product;
	}
}
