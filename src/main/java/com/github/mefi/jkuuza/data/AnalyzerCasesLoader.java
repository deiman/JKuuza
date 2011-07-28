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
		extractionRule1.add(BasicProductProperties.PARAMETER_NAME.toString(), "#tParametry span.label");
		extractionRule1.add(BasicProductProperties.PARAMETER_VALUE.toString(), "#tParametry span.value");

		Case case1 = new Case(conditions1, extractionRule1);
		case1.setPreviewUrl("http://www.vkfoto.cz/nikon-28-300-mm-f-3-5-5-6g-ed-af-s-vr/");

		map.put("vkfoto.cz", case1);


		
		List<Condition> conditions2 = new ArrayList<Condition>();
		// sample data
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "aaa"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasTag", "true", "bbb"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "ccc"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "ddd"));

		Rules extractionRule2 = new Rules();
		extractionRule2.add(BasicProductProperties.NAME.toString(), "aaa");
		extractionRule2.add(BasicProductProperties.DESCRIPTION.toString(), "bbb");
		extractionRule2.add(BasicProductProperties.PRICE.toString(), "ccc");
		extractionRule2.add(BasicProductProperties.PRICE_DPH.toString(), "ddd");
		extractionRule2.add(BasicProductProperties.PARAMETER_NAME.toString(), "eee");
		extractionRule2.add(BasicProductProperties.PARAMETER_VALUE.toString(), "fff");

		Case case2 = new Case(conditions2, extractionRule2);
		case2.setPreviewUrl("http://czc.cz/index.php");
		map.put("czc.cz", case2);

		return map;
	}

}
