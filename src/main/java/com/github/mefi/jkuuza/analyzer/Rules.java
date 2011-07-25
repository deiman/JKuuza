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
		if(map.get(property) == null) {
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
}