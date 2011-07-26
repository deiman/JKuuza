package com.github.mefi.jkuuza.analyzer;

import org.jsoup.nodes.Document;

/**
 *
 * @author Marek Pilecky
 */
public abstract class Resolver {

	abstract boolean resolve(Document doc);
}
