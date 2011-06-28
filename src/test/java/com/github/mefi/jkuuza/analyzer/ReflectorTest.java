/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.mefi.jkuuza.analyzer;

import java.util.Collection;
import java.util.List;
import com.github.mefi.jkuuza.analyzer.anotation.MethodInfo;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author mefi
 */
public class ReflectorTest {

	public ReflectorTest() {
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
	 * Test of getDeclaredMethodsAndParams method, of class Reflector.
	 */
	@Test
	public void testGetDeclaredMethodsAndParams() {
		System.out.println("getDeclaredMethodsAndParams");

		Collection<Method> expected = new ArrayList<Method>();
		Method method = new Method();
		method.setName("foo");
		method.setDescription("bar");
		method.setReturnType("boolean");
		method.addParameter("baz", "String");
		method.addParameter("qux", "Integer");
		expected.add(method);

		Reflector reflector = new Reflector();

		Collection<Method> result = reflector.getDeclaredMethodsAndParams(TestClass2.class);

		for (int i = 0; i < expected.size(); i++) {
			Method ex = expected.iterator().next();
			Method res = result.iterator().next();

			assertEquals(ex.getName(), res.getName());
			assertEquals(ex.getDescription(), res.getDescription());
			assertEquals(ex.getReturnType(), res.getReturnType());
			assertEquals(ex.getParameters().size(), res.getParameters().size());
		}

		//assertEquals(expected, result);
		//assert(expected, );
		//assertThat(result, both(hasItems(expected)).and(hasSize(expected.size())));
	}

	/**
	 * Test of call method, of class Reflector.
	 * call(Object o, String methodName, Object[] params)
	 */
	@Test
	public void testCall() throws Exception {
		System.out.println("call(Object o, String methodName, Object[] params)");

		TestClass1 myTestClass = new TestClass1();

		Object params1[] = {new String("foo"), new String("bar")};
		assertEquals("1", Reflector.call(myTestClass, "methodWithTwoParams", params1).toString());

		Object params2[] = {"foo", "bar"};
		assertEquals("1", Reflector.call(myTestClass, "methodWithTwoParams", params2).toString());

		Object params3[] = {1, "bar"};
		assertEquals("2", Reflector.call(myTestClass, "methodWithTwoParams", params3).toString());

		Object params4[] = {"foo", 1};
		assertEquals("3", Reflector.call(myTestClass, "methodWithTwoParams", params4).toString());

		Object params5[] = {new Integer(1), new Integer(2)};
		assertEquals("4", Reflector.call(myTestClass, "methodWithTwoParams", params5).toString());

		Object params6[] = {1, 2};
		assertEquals("4", Reflector.call(myTestClass, "methodWithTwoParams", params6).toString());

		System.out.println("call(Object o, String methodName, Object param1, Object param2)");

		assertEquals("foo+bar", Reflector.call(myTestClass, "methodWithTwoParamsAndReturnValue", "foo", "bar").toString());

	}

	public class TestClass1 {

		public TestClass1() {
		}

		public String methodWithTwoParams(String s1, String s2) {
			return "1";
		}

		public String methodWithTwoParams(Integer i, String s) {
			return "2";
		}

		public String methodWithTwoParams(String s, Integer i) {
			return "3";
		}

		public String methodWithTwoParams(Integer i1, Integer i2) {
			return "4";
		}

		public String methodWithTwoParamsAndReturnValue(String s1, String s2) {
			return s1 + "+" + s2;
		}
	}

	public class TestClass2 {

		public TestClass2() {
		}

		@MethodInfo(description="bar", parameters="baz, qux")
		public boolean foo(String baz, Integer qux) {
			return true;
		}
	}


}
