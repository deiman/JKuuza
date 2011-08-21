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

import com.github.mefi.jkuuza.parser.ContentHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds object and its function name, parameters of it and expected return value 
 *
 * @author Marek Pilecky
 */
public class Condition {

	private ContentHelper object;
	private String functionName;
	private String expectedValue;
	private List<String> params;

	public Condition() {
	}

	public Condition(ContentHelper conditionObject, String functionName, String expectedValue, List<String> params) {
		this.object = conditionObject;
		this.functionName = functionName;
		this.expectedValue = expectedValue;
		this.params = params;
	}

	public Condition(ContentHelper conditionObject, String functionName, String expectedValue, String param) {
		this.object = conditionObject;
		this.functionName = functionName;
		this.expectedValue = expectedValue;
		this.params = new ArrayList<String>();
		params.add(param);
	}

	public Condition(ContentHelper conditionObject, String functionName, String expectedValue, String param1, String param2) {
		this.object = conditionObject;
		this.functionName = functionName;
		this.expectedValue = expectedValue;
		this.params = new ArrayList<String>();
		params.add(param1);
		params.add(param2);
	}

	public ContentHelper getConditionObject() {
		return object;
	}

	public void setConditionObject(ContentHelper object) {
		this.object = object;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}
}
