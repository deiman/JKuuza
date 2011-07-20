package com.github.mefi.jkuuza.analyzer;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import org.jsoup.nodes.Document;

/**
 * Makes decision if Document is passing all conditions
 *
 * @author Marek Pilecky
 */
public class ConditionsResolver {

	List<Condition> conditions;

	public ConditionsResolver(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	/**
	 * Apply all conditions on this Document
	 *
	 * @param doc Jsoup Document with html code of a web page
	 * @return true if ALL conditions pass, false if AT LEAST ONE not pass
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public boolean isPassing(Document doc) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

		boolean okStatus = true;
		for (Iterator<Condition> it = conditions.iterator(); it.hasNext();) {
			Condition condition = it.next();
			
			String[] params = new String[condition.getParams().size()];
			condition.getParams().toArray(params);

			Object result = Reflector.call(condition.getConditionObject(), condition.getFunctionName(), params);
			if(!result.toString().equals(condition.getExpectedValue())) {
				okStatus = false;
			}
		}
		return okStatus;
	}

}
