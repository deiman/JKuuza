package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.parser.ContentHelper;
import java.util.List;

/**
 * Holds object and its function name, parameters of it and expected return value 
 *
 * @author Marek Pilecky
 */
public class Condition {

	private Object object;
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

	public Object getConditionObject() {
		return object;
	}

	public void setConditionObject(Object object) {
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
