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
