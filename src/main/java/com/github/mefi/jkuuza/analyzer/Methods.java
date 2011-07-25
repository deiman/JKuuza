package com.github.mefi.jkuuza.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates list with Method instances and provide functions for better use
 *
 * @author Marek Pilecky
 */
public class Methods {

	private List<Method> list;

	/**
	 * Create instance
	 */
	public Methods() {
		list = new ArrayList<Method>();
	}

	/**
	 * Create instance and fill it from collection
	 *
	 * @param collection with Method instances
	 */
	public Methods(Collection<Method> collection) {
		list = new ArrayList<Method>(collection);
	}

	/**
	 * Get the value of list
	 *
	 * @return the value of list
	 */
	public List<Method> getList() {
		return list;
	}

	/**
	 * Set the value of list
	 *
	 * @param methods new value of list
	 */
	public void setList(List<Method> list) {
		this.list = list;
	}

	/**
	 * Add method into list
	 *
	 * @param method
	 */
	public void add(Method method) {
		this.list.add(method);
	}

	/**
	 * Find method by name and return it
	 *
	 * @param methodName Name of the method
	 * @return method
	 * @throws IllegalArgumentException if method not found
	 */
	public Method get(String methodName) {
		for (Iterator<Method> it = list.iterator(); it.hasNext();) {
			Method method = it.next();
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Method not found");
	}

}
