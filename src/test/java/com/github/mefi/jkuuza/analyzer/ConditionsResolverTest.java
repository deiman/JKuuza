package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.parser.ContentHelper;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mefi
 */
public class ConditionsResolverTest {

    public ConditionsResolverTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

	/**
	 * Test of isPassing method, of class ConditionsResolver.
	 */ @Test
	public void testisPassing() throws Exception {
		System.out.println("isPassing");

		// common
		TestClass1 testClass1 = new TestClass1();
		//empty doc
		Document doc = new Document("http://example.com");		

		
		List<Condition> conditions1 = new ArrayList<Condition>();
		List<String> params1 = new ArrayList<String>();
		params1.add("bar");
		Condition condition1 = new Condition(testClass1, "returnTrue", "true", params1);
		conditions1.add(condition1);
		ConditionsResolver resolver1 = new ConditionsResolver(conditions1);

		assertTrue("test1", resolver1.isPassing(doc));

		List<Condition> conditions2 = new ArrayList<Condition>();
		List<String> params2 = new ArrayList<String>();
		params2.add("bar");
		Condition condition2 = new Condition(testClass1, "returnFalse", "false", params2);
		conditions2.add(condition2);
		ConditionsResolver resolver2 = new ConditionsResolver(conditions2);

		assertTrue("test2", resolver2.isPassing(doc));

		List<Condition> conditions3 = new ArrayList<Condition>();
		List<String> params3 = new ArrayList<String>();
		params3.add("bar");
		Condition condition3 = new Condition(testClass1, "returnFalse", "true", params3);
		conditions3.add(condition3);
		ConditionsResolver resolver3 = new ConditionsResolver(conditions3);

		assertFalse("test3",resolver3.isPassing(doc));

		List<Condition> conditions4 = new ArrayList<Condition>();
		List<String> params4 = new ArrayList<String>();
		params4.add("bar");
		Condition condition4 = new Condition(testClass1, "returnString", "bar", params4);
		conditions4.add(condition4);
		ConditionsResolver resolver4 = new ConditionsResolver(conditions4);

		assertTrue("test4", resolver4.isPassing(doc));

		// test all
		List<Condition> conditionsAll = new ArrayList<Condition>();
		conditionsAll.add(condition1);
		conditionsAll.add(condition2);
		conditionsAll.add(condition3); // this returns false
		conditionsAll.add(condition4);
		ConditionsResolver resolverAll1 = new ConditionsResolver(conditionsAll);

		assertFalse("testAll", resolverAll1.isPassing(doc));

		conditionsAll.remove(condition3); //because this one returns false, so others are now true
		ConditionsResolver resolverAll2 = new ConditionsResolver(conditionsAll);

		assertTrue("testAll", resolverAll2.isPassing(doc));

	}


	 public class TestClass1 extends ContentHelper {

		public TestClass1() {
		}

		public boolean returnTrue(String foo) {
			return true;
		}
	
		public boolean returnFalse(String foo) {
			return false;
		}

		public String returnString(String s) {
			return s;
		}

	}
}