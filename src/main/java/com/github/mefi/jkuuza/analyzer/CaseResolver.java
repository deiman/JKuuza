package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.BodyContentRepository;
import com.github.mefi.jkuuza.model.PageRepository;
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

	public CaseResolver(Case casex) {
		this.casex = casex;
	}

	public void resolve(String host) throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		DbConnector conn = new DbConnector();
		PageRepository pageRepository = new PageRepository(conn.getConnection());
		BodyContentRepository bodyContentRepository = new BodyContentRepository(conn.getConnection());

		List<String> list = pageRepository.findUrlsByHost(host);


		ConditionsResolver conditionsResolver = new ConditionsResolver(casex.getConditions());
		ExtractionResolver extractionResolver = new ExtractionResolver(casex.getRules());

		for (int i = 0; i < list.size(); i++) {

			BodyContent bodyContent = bodyContentRepository.findByUrl(list.get(i));
			Document doc = Jsoup.parse(bodyContent.getBodyHtml());

			if (conditionsResolver.resolve(doc)) {
				//TODO: - implement getting od values
				extractionResolver.resolve(doc);
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
