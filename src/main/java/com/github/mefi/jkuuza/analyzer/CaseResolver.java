package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.analyzer.gui.AnalyzerConsole;
import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.gui.AppView;
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.BodyContentRepository;
import com.github.mefi.jkuuza.model.PageRepository;
import com.github.mefi.jkuuza.model.Product;
import com.github.mefi.jkuuza.model.ProductController;
import com.github.mefi.jkuuza.model.ProductRepository;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Marek Pilecky
 */
public class CaseResolver {

	private Case casex;
	private DbConnector connector;
	private int countOfExtracted = 0;
	private int countOfProcessed = 0;
	
	public CaseResolver(Case casex, DbConnector dbConnector) {
		this.casex = casex;
		this.connector = dbConnector;
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

	
		
		PageRepository pageRepository = new PageRepository(connector.getConnection());
		BodyContentRepository bodyContentRepository = new BodyContentRepository(connector.getConnection());
		ProductRepository productRepository = new ProductRepository(connector.getConnection());
		ProductController productController = new ProductController(connector.getConnection());

		List<String> list = pageRepository.findUrlsByHost(host);


		ConditionsResolver conditionsResolver = new ConditionsResolver(casex.getConditions());
		ExtractionResolver extractionResolver = new ExtractionResolver(casex.getRules());

		AppView.getInstance().getAnalyzerCountOfTotal().setText(String.valueOf(list.size()));

		for (int i = 0; i < list.size(); i++) {

			BodyContent bodyContent = bodyContentRepository.findByUrl(list.get(i));
			Document doc = Jsoup.parse(bodyContent.getBodyHtml());

			if (conditionsResolver.resolve(doc)) {
				Product product = extractionResolver.resolve(doc);
				product.setUrl(list.get(i));
				AnalyzerConsole.print(product.getName());

				productController.save(product);
				AppView.getInstance().getAnalyzerCountOfExtracted().setText(String.valueOf(++countOfExtracted));
			}

			AppView.getInstance().getAnalyzerCountOfProcessed().setText(String.valueOf(++countOfProcessed));
			
			if (i+1 == list.size()) {
				AnalyzerConsole.printNewLine();
				AnalyzerConsole.print("Extrakce dokončena", true);
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
