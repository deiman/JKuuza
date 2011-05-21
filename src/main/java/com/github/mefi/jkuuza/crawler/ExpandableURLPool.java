/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.mefi.jkuuza.crawler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.niocchi.core.URLPool;
import org.niocchi.core.URLPoolException;
import org.niocchi.core.query.Query;

/**
 *
 * @author Marek Pilecky
 */
public class ExpandableURLPool implements URLPool {

	List<String> unprocessedUrls = new ArrayList<String>();
	Set<String> processedUrls = new HashSet<String>();
	int cursor = 0;
	int outstandingQueryies = 0;
	int count = 0;

	public ExpandableURLPool(Collection<String> seedURLs) {
		this.unprocessedUrls.addAll(seedURLs);
	}

	/**
	 * Checks if there are urls to crawle
	 * 
	 * @return true, when there are unprocessed urls or when URLPool is waiting
	 * to resources to process
	 */
	@Override
	public boolean hasNextQuery() {
		return cursor < unprocessedUrls.size() || outstandingQueryies > 0;
	}

	/**
	 * Returns Query to crawle
	 *
	 * @return query
	 * @throws URLPoolException
	 */
	@Override
	public Query getNextQuery() throws URLPoolException {

		try {
			if (cursor >= unprocessedUrls.size()) {
				return null;
			} else {


				while (processedUrls.contains(unprocessedUrls.get(cursor))) {
					cursor++;
				}
				String url = unprocessedUrls.get(cursor);
				Query query = new Query(url);

				outstandingQueryies++;
				processedUrls.add(unprocessedUrls.get(cursor));
				return new Query(unprocessedUrls.get(cursor++));

			}
		} catch (MalformedURLException e) {
			throw new URLPoolException("invalid url", e);
		}

	}


	/**
	 * Sets Query as processed, so outstangingQueries count is minus 1
	 * @param query Query which has been crawled
	 */
	@Override
	public void setProcessed(Query query) {
		outstandingQueryies--;
		dump(query);

	}

	/**
	 * if isn't url already processed and isn't in waiting queue, adds it to queue
	 *
	 * @param url String with url
	 */
	public void addURL(String url) {
		if (!processedUrls.contains(url) && !unprocessedUrls.contains(url)) {
			unprocessedUrls.add(url);
		}
	}

	/**
	 * Writes Query url into output 
	 * @param query
	 */
	private void dump(Query query) {
		System.out.println(++count + " - " + query.getOriginalURL());
	}
}
