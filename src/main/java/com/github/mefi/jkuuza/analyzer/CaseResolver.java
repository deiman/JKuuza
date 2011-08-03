package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.analyzer.gui.AnalyzerConsole;
import com.github.mefi.jkuuza.app.db.CouchDbConnectionException;
import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.BodyContentRepository;
import com.github.mefi.jkuuza.model.PageRepository;
import com.github.mefi.jkuuza.model.Product;
import com.github.mefi.jkuuza.model.ProductController;
import com.github.mefi.jkuuza.model.ProductRepository;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Marek Pilecky
 */
public class CaseResolver {

	private Case casex;
	
	public CaseResolver(Case casex) {
		this.casex = casex;
	}

	/**
	 * Finds all documents in db having specified host. Firstly applies all conditions from case to
	 * each of them. If pass, then Rules ale applied
	 *
	 * @param host 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void resolve(String host) throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		DbConnector conn = null;
		try {
			conn = new DbConnector();
		} catch (CouchDbConnectionException ex) {
			Logger.getLogger(CaseResolver.class.getName()).log(Level.SEVERE, null, ex);
		}
		PageRepository pageRepository = new PageRepository(conn.getConnection());
		BodyContentRepository bodyContentRepository = new BodyContentRepository(conn.getConnection());
		ProductRepository productRepository = new ProductRepository(conn.getConnection());
		ProductController productController = new ProductController(conn.getConnection());

		List<String> list = pageRepository.findUrlsByHost(host);


		ConditionsResolver conditionsResolver = new ConditionsResolver(casex.getConditions());
		ExtractionResolver extractionResolver = new ExtractionResolver(casex.getRules());

		for (int i = 0; i < list.size(); i++) {

			BodyContent bodyContent = bodyContentRepository.findByUrl(list.get(i));
			Document doc = Jsoup.parse(bodyContent.getBodyHtml());

			if (conditionsResolver.resolve(doc)) {
				Product product = extractionResolver.resolve(doc);
				product.setUrl(list.get(i));
				AnalyzerConsole.print(product.getName());

				productController.save(product);
			}
		}
	}

	public void setCasex(Case casex) {
		this.casex = casex;
	}

	public Case getCasex() {
		return casex;
	}
}
