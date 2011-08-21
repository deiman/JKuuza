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

package com.github.mefi.jkuuza.data;

import com.github.mefi.jkuuza.analyzer.Case;
import com.github.mefi.jkuuza.analyzer.Condition;
import com.github.mefi.jkuuza.analyzer.Rules;
import com.github.mefi.jkuuza.model.BasicProductProperties;
import com.github.mefi.jkuuza.parser.ContentAnalyzer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marek Pilecky
 */
public class AnalyzerCasesLoader {

	public static Map<String, Case> load() {
		// TODO: implement loading from resources
		
		Map<String, Case> map = new HashMap<String, Case>();
		
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "rezervace"));
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "upc"));
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasClass", "false", "foobarbaz"));
		
		Rules extractionRule1 = new Rules();
		extractionRule1.add(BasicProductProperties.NAME, ".detail h1");
		extractionRule1.add(BasicProductProperties.DESCRIPTION.toString(), ".text-content p[align=justify]");
		extractionRule1.add(BasicProductProperties.PRICE.toString(), ".cenabez span");
		extractionRule1.add(BasicProductProperties.PRICE_DPH.toString(), ".cenas span");
		extractionRule1.add(BasicProductProperties.TYPE.toString(), ".seznam > .selected > a");
		extractionRule1.add(BasicProductProperties.PRODUCER.toString(), ".content .vyrobce a");
		extractionRule1.add(BasicProductProperties.PARAMETER_NAME.toString(), "#tParametry span.label");
		extractionRule1.add(BasicProductProperties.PARAMETER_VALUE.toString(), "#tParametry span.value");

		Case case1 = new Case(conditions1, extractionRule1);
		case1.setPreviewUrl("http://www.vkfoto.cz/nikon-28-300-mm-f-3-5-5-6g-ed-af-s-vr/");

		map.put("vkfoto.cz", case1);


		
		List<Condition> conditions2 = new ArrayList<Condition>();
		// sample data
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "col-availability"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "detail-info"));
		List<String> conditions2Params1 = new ArrayList();
		conditions2Params1.add("input");
		conditions2Params1.add("title");
		conditions2Params1.add("Přidat produkt do nákupního košíku");
		conditions2.add(new Condition(new ContentAnalyzer(), "tagHasAttributeWithValue", "true", conditions2Params1));

		Rules extractionRule2 = new Rules();
		extractionRule2.add(BasicProductProperties.NAME.toString(), "div#content div#add-box div.in div.spc h1");
		extractionRule2.add(BasicProductProperties.DESCRIPTION.toString(), "div.detail-box tr.without-dph > td.right");
		extractionRule2.add(BasicProductProperties.PRICE.toString(), "div.detail-box tr.our-prize > td.prize");
		extractionRule2.add(BasicProductProperties.PRICE_DPH.toString(), "div.detail-box tr.our-prize > td.prize");
		extractionRule2.add(BasicProductProperties.TYPE.toString(), "div#product-menu ul li.expanded ul li.active a");
		extractionRule2.add(BasicProductProperties.PRODUCER.toString(), "div.spc div.detail-box div.box-spc div.col-r p span.l a");
		extractionRule2.add(BasicProductProperties.PARAMETER_NAME.toString(), "div.spc div#specifikace table.params tbody tr td.first");
		extractionRule2.add(BasicProductProperties.PARAMETER_VALUE.toString(), "div.spc div#specifikace table.params tbody tr td");

		Case case2 = new Case(conditions2, extractionRule2);
		case2.setPreviewUrl("http://www.megapixel.cz/canon-eos-600d-18-55-mm-is-ii");
		map.put("megapixel.cz", case2);


		List<Condition> conditions3 = new ArrayList<Condition>();
		// sample data
		conditions3.add(new Condition(new ContentAnalyzer(), "tagHasValue", "true", "a", "KOUPIT"));
		conditions3.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "detailPrice"));

		Rules extractionRule3 = new Rules();
		extractionRule3.add(BasicProductProperties.NAME.toString(), "div#page div#panel div#obsah h1");
		extractionRule3.add(BasicProductProperties.DESCRIPTION.toString(), "div#page div#panel div#obsah div.detailText div.detailPriceInfo p.font11");
		extractionRule3.add(BasicProductProperties.PRICE.toString(), "div#page div#panel div#obsah div.detailText div.detailPriceInfo div.detailPrice div.number");
		extractionRule3.add(BasicProductProperties.PRICE_DPH.toString(), "div#page div#panel div#obsah2 div#pop.domtab div.detailPopis p");
		extractionRule3.add(BasicProductProperties.TYPE.toString(), "");
		extractionRule3.add(BasicProductProperties.PRODUCER.toString(), "");
		extractionRule3.add(BasicProductProperties.PARAMETER_NAME.toString(), "div#panel div#obsah2 div#par.domtab table.datailTable tbody tr.dark th");
		extractionRule3.add(BasicProductProperties.PARAMETER_VALUE.toString(), "div#panel div#obsah2 div#par.domtab table.datailTable tbody tr.dark td");

		Case case3 = new Case(conditions3, extractionRule3);
		case3.setPreviewUrl("http://www.fotoskoda.cz/eobchod/canon-eos-600d-efs-18-55-is-ii-55-250");
		map.put("fotoskoda.cz", case3);

		return map;
	}

}
