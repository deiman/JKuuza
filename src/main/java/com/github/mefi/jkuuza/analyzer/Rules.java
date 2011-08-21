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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marek Pilecky
 */
public class Rules {

	Map<String, String> map;

	public Rules() {
		map = new HashMap<String, String>();
	}

	/**
	 * Add property and its selector
	 *
	 * @param property
	 * @param selector
	 */
	public void add(String property, String selector) {
		map.put(property, selector);
	}

	/**
	 * Add property and its selector
	 *
	 * @param property
	 * @param selector
	 */
	public void add(BasicProductProperties property, String selector) {
		map.put(property.toString(), selector);
	}

	/**
	 * Return selector by property name
	 *
	 * @param property
	 * @return selector or empty string if property dosn't exists
	 */
	public String getSelector(String property) {
		if (map.get(property) == null) {
			return "";
		}
		return map.get(property);
	}

	/**
	 * Return selector by property name
	 *
	 * @param property
	 * @return selector or empty string if property dosn't exists
	 */
	public String getSelector(BasicProductProperties property) {
		return getSelector(property.toString());
	}

	/**
	 * Returns map with rules values
	 *
	 * @return map where key is property name and value is selector
	 */
	public Map<String, String> getValues() {
		return map;
	}
}
