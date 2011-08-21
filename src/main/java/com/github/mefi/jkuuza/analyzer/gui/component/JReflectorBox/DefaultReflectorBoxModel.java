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

package com.github.mefi.jkuuza.analyzer.gui.component.JReflectorBox;

import com.github.mefi.jkuuza.analyzer.Methods;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marek Pilecky
 */
public class DefaultReflectorBoxModel implements IReflectorBoxModel {

	private Methods methods;

	private String className;
	private String methodName;
	private String expected;
	private List<String> params;


	/**
	 * Create instance of default model
	 *
	 * @param methods
	 */
	public DefaultReflectorBoxModel(Methods methods) {
		this.methods = methods;
		params = new ArrayList<String>();
	}

	/**
	 * Get the value of methods
	 *
	 * @return methods
	 */
	@Override
	public Methods getMethods() {
		return methods;
	}

	/**
	 * Set the value of methods
	 *
	 * @param methods
	 */
	@Override
	public void setMethods(Methods methods) {
		this.methods = methods;
	}

	/**
	 * Get the value of methodName
	 *
	 * @return methodName
	 */
	@Override
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Set the value of methodName
	 *
	 * @param methodName
	 */
	@Override
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Get the value of params
	 *
	 * @return params
	 */
	@Override
	public List<String> getParams() {
		return params;
	}

	/**
	 * Set the value of params
	 *
	 * @param params
	 */
	@Override
	public void setParams(List<String> params) {
		this.params = params;
	}

	/**
	 * Get the value of expected
	 *
	 * @return expected
	 */
	@Override
	public String getExpected() {
		return this.expected;
	}

	/**
	 * Set the value of expected
	 *
	 * @param value
	 */
	@Override
	public void setExpected(String value) {
		this.expected = value;
	}

	/**
	 * Get the value of className
	 *
	 * @return className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Set the value of className
	 *
	 * @param name
	 */
	public void setClassName(String name) {
		this.className = name;
	}

}
