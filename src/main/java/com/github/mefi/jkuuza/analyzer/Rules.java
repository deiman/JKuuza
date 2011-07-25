package com.github.mefi.jkuuza.analyzer;

/**
 *
 * @author Marek Pilecky
 */
public class Rules {

	private String nameSelector;
	private String priceSelector;
	private String priceDPHSelector;
	private String descriptionSelector;
	private String paramNamesSelector;
	private String paramValuesSelector;

	public String getDescriptionSelector() {
		return descriptionSelector;
	}

	public void setDescriptionSelector(String descriptionSelector) {
		this.descriptionSelector = descriptionSelector;
	}

	public String getNameSelector() {
		return nameSelector;
	}

	public void setNameSelector(String nameSelector) {
		this.nameSelector = nameSelector;
	}

	public String getParamNamesSelector() {
		return paramNamesSelector;
	}

	public void setParamNamesSelector(String paramNamesSelector) {
		this.paramNamesSelector = paramNamesSelector;
	}

	public String getParamValuesSelector() {
		return paramValuesSelector;
	}

	public void setParamValuesSelector(String paramValuesSelector) {
		this.paramValuesSelector = paramValuesSelector;
	}

	public String getPriceDPHSelector() {
		return priceDPHSelector;
	}

	public void setPriceDPHSelector(String priceDPHSelector) {
		this.priceDPHSelector = priceDPHSelector;
	}

	public String getPriceSelector() {
		return priceSelector;
	}

	public void setPriceSelector(String priceSelector) {
		this.priceSelector = priceSelector;
	}
}
