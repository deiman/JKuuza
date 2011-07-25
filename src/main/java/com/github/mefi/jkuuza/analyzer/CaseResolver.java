package com.github.mefi.jkuuza.analyzer;

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

	public boolean resolve(Document doc) {
		// TODO: implemen


		return false;
	}

	public void setCasex(Case casex) {
		this.casex = casex;
	}

	public Case getCasex() {
		return casex;
	}

}
