/*
 * Efficient crawl library implemented with NIO.
 * http://www.niocchi.com
 * Copyright (C) 2009 François-Louis Mommens.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.mefi.jkuuza.crawler;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.niocchi.core.URLPool;
import org.niocchi.core.URLPoolException;
import org.niocchi.core.Query;
import org.niocchi.core.QueryStatus;

/**
 * this class is an URLPool wrapper that drops all subsequent Queries
 * from hosts that have reached too many consecutive timeouts.
 * Implementation detail: the dropQuery methods calls
 * _url_pool.setProcessed with a singleton Resource by assuming
 * _url_pool will not store it.
 *
 * @author FL Mommens
 * @author Marek Pilecky
 */
public class TimeoutURLPool implements URLPool {

	private static Log _logger = LogFactory.getLog(TimeoutURLPool.class);
	protected static int _DEFAULT_MAX_TIMEOUTS = 10;
	URLPool _url_pool;
	int _max_timeouts = _DEFAULT_MAX_TIMEOUTS;
	HashMap<String, Integer> _timeout_map = new HashMap<String, Integer>(); // number of timeout per website

	// ------------------------------------------------------------
	public TimeoutURLPool(URLPool pool_) {
		_url_pool = pool_;
	}

	// ------------------------------------------------------------
	public boolean hasNextQuery() {
		return _url_pool.hasNextQuery();
	}

	// ------------------------------------------------------------
	public Query getNextQuery() throws URLPoolException {
		while (_url_pool.hasNextQuery()) {
			Query query = _url_pool.getNextQuery();
			if (query == null) {
				return null;	// no query ready yet
			}
			Integer ti = _timeout_map.get(query.getHost());
			if (ti == null) {
				ti = 0;
			}

			if (ti >= _max_timeouts) {
				_logger.debug("Dropping URL[ " + query.getURL() + " ]");
				query.setStatus(QueryStatus.DROPPED);

				// We send the query back because
				// the URLPools has to receive all
				// the processed or dropped queries
				_url_pool.setProcessed(query);
				continue;
			}

			return query;
		}

		return null;
	}

	// ------------------------------------------------------------
	public void setProcessed(Query query) {
		String host = query.getHost();

		if (query.getStatus() == QueryStatus.TIMEOUT) {
			Integer ti = _timeout_map.get(host);
			if (ti == null) {
				ti = 0;
			}
			ti++;
			_timeout_map.put(host, ti);
			if (ti >= _max_timeouts) {
				_logger.info("Maximun timeouts reached per Host[ " + query.getURL().getProtocol() + "://" + host + " ] It will be dropped.");
			}
		} else {
			_timeout_map.remove(host);
		}

		_url_pool.setProcessed(query);
	}

	// ------------------------------------------------------------
	public void setMaxConsecutiveTimeouts(int max_) {
		_max_timeouts = max_;
	}

	public URLPool getUrlPool() {
		return _url_pool;
	}
}
