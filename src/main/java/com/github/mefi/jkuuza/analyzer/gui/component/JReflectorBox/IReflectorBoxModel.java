package com.github.mefi.jkuuza.analyzer.gui.component.JReflectorBox;

import com.github.mefi.jkuuza.analyzer.Methods;
import java.util.List;

/**
 *
 * @author Marek Pilecky
 */
public interface IReflectorBoxModel {

	public Methods getMethods();

	public void setMethods(Methods methods);

	public String getClassName();

	public void setClassName(String name);

	public String getMethodName();

	public void setMethodName(String name);

	public List<String> getParams();

	public void setParams(List<String> params);

	public String getExpected();

	public void setExpected(String value);
}
