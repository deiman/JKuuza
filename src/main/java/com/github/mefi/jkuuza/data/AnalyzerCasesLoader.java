package com.github.mefi.jkuuza.data;

import com.github.mefi.jkuuza.analyzer.Case;
import com.github.mefi.jkuuza.analyzer.Condition;
import com.github.mefi.jkuuza.analyzer.Rules;
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
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "banner"));
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasTag", "true", "h1"));
		conditions1.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "footer"));
		
		Rules extractionRule1 = new Rules();
		extractionRule1.setNameSelector("aaa");
		extractionRule1.setPriceSelector("bbb");
		extractionRule1.setPriceDPHSelector("ccc");
		extractionRule1.setDescriptionSelector("ddd");
		extractionRule1.setParamNamesSelector("eee");
		extractionRule1.setParamValuesSelector("fff");

		Case case1 = new Case(conditions1, extractionRule1);
		case1.setPreviewUrl("http://vkfoto.cz/index.php");

		map.put("vkfoto.cz", case1);


		
		List<Condition> conditions2 = new ArrayList<Condition>();
		// sample data
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "aaa"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasTag", "true", "bbb"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "ccc"));
		conditions2.add(new Condition(new ContentAnalyzer(), "docHasClass", "true", "ddd"));

		Rules extractionRule2 = new Rules();
		extractionRule2.setNameSelector("bla");
		extractionRule2.setPriceSelector("ble");
		extractionRule2.setPriceDPHSelector("bli");
		extractionRule2.setDescriptionSelector("blo");
		extractionRule2.setParamNamesSelector("blu");
		extractionRule2.setParamValuesSelector("blah");

		Case case2 = new Case(conditions2, extractionRule2);
		case2.setPreviewUrl("http://czc.cz/index.php");
		map.put("czc.cz", case2);

		return map;
	}

}
