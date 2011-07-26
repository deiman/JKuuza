package com.github.mefi.jkuuza.utils;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author Marek Pilecky
 */
public class ValueComparator implements Comparator {

	Map map;

	public ValueComparator(Map base) {
		this.map = base;
	}

	public int compare(Object a, Object b) {
		if ((Integer) map.get(a) < (Integer) map.get(b)) {
			return 1;
		} else if ((Integer) map.get(a) == (Integer) map.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}
