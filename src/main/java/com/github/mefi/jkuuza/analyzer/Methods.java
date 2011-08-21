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
