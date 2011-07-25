package com.github.mefi.jkuuza.analyzer;

import java.util.List;

/**
 *
 * @author Marek Pilecky
 */
public class Case {

	private List<Condition> conditions;
	private Rules rules;
	private String previewUrl;
	

	public Case(List<Condition> conditions, Rules rules) {
		this.conditions = conditions;
		this.rules = rules;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	
}
