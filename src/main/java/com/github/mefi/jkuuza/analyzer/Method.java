package com.github.mefi.jkuuza.analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Marek Pilecky
 */
public class Method {

	private String packageName;
	private String className;
	private String name;
	private String description;
	private String returnType;
	private Map<String, String> parameters;

	public Method() {
		parameters = new LinkedHashMap<String, String>();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Add parameter into collection. Key - name, value - type
	 *
	 * @param name name of variable
	 * @param type type of variable
	 */
	public void addParameter(String name, String type) {
		parameters.put(name, type);
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	

}
