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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
	List<Condition> failedConditions;

	public ConditionsResolver(List<Condition> conditions) {
		this.conditions = conditions;
		failedConditions = new ArrayList();
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
	public boolean resolve(Document doc) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

		boolean okStatus = true;
		for (Iterator<Condition> it = conditions.iterator(); it.hasNext();) {
			Condition condition = it.next();

			String[] params = new String[condition.getParams().size()];
			condition.getParams().toArray(params);
			condition.getConditionObject().setDocument(doc);

			Object result = Reflector.call(condition.getConditionObject(), condition.getFunctionName(), params);
			if (!result.toString().equals(condition.getExpectedValue())) {
				okStatus = false;
				failedConditions.add(condition);
			}
		}
		return okStatus;
	}

	public List<Condition> getFailedConditions() {
		return failedConditions;
	}
}
