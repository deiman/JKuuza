package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.model.BodyContent;
import com.github.mefi.jkuuza.model.BodyContentRepository;
import com.github.mefi.jkuuza.model.Page;
import com.github.mefi.jkuuza.model.PageRepository;
import java.util.List;


/**
 *
 * @author Marek Pilecky
 */
public class TestAnalyzer {


	public static void main(String[] args) {
		DbConnector conn = new DbConnector();
		PageRepository pageRepository = new PageRepository(conn.getConnection());
		BodyContentRepository bodyContentRepository = new BodyContentRepository(conn.getConnection());

		BodyContent bodyContent = bodyContentRepository.findByUrl("http://www.vkfoto.cz/lowepro-adventura-uz-100-black/");

		System.out.println(bodyContent.getBodyHtml());
		
	}

}
