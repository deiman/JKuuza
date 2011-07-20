/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.mefi.jkuuza.analyzer;

import java.util.Collection;
import com.github.mefi.jkuuza.parser.MethodInfo;
import java.util.ArrayList;
import java.util.Iterator;
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
	public void testGetDeclaredMethods() {
		System.out.println("getDeclaredMethods");

		Methods expected = new Methods();
		Method method = new Method();
		method.setPackageName("com.github.mefi.jkuuza.analyzer");
		method.setClassName("ReflectorTest$TestClass2");
		method.setName("foo");
		method.setDescription("bar");
		method.setReturnType("boolean");
		method.addParameter("baz", "String");
		method.addParameter("qux", "Integer");
		expected.add(method);

		Method method2 = new Method();
		method2.setPackageName("com.github.mefi.jkuuza.analyzer");
		method2.setClassName("ReflectorTest$TestClass2");
		method2.setName("foo2");
		method2.setDescription("bar2");
		method2.setReturnType("String");
		method2.addParameter("baz2", "String");
		expected.add(method2);

		Methods result = Reflector.getDeclaredMethodsWithInfo(TestClass2.class);

		assertEquals(expected.getList().size(), result.getList().size());

		Method ex = null;
		Method res = null;
		for (int i = 0; i < expected.getList().size(); i++) {
			ex = expected.getList().get(i);
			res = result.getList().get(i);
			assertEquals(ex.getPackageName(), res.getPackageName());
			assertEquals(ex.getClassName(), res.getClassName());			
			assertEquals(ex.getName(), res.getName());
			assertEquals(ex.getDescription(), res.getDescription());
			assertEquals(ex.getReturnType(), res.getReturnType());
			assertEquals(ex.getParameters().size(), res.getParameters().size());
		}
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
		assertEquals("t1", "1", Reflector.call(myTestClass, "methodWithTwoParams", params1).toString());

		Object params2[] = {"foo", "bar"};
		assertEquals("t2", "1", Reflector.call(myTestClass, "methodWithTwoParams", params2).toString());

		Object params3[] = {1, "bar"};
		assertEquals("t3", "2", Reflector.call(myTestClass, "methodWithTwoParams", params3).toString());

		Object params4[] = {"foo", 1};
		assertEquals("t4", "3", Reflector.call(myTestClass, "methodWithTwoParams", params4).toString());

		Object params5[] = {new Integer(1), new Integer(2)};
		assertEquals("t5", "4", Reflector.call(myTestClass, "methodWithTwoParams", params5).toString());

		Object params6[] = {1, 2};
		assertEquals("t6", "4", Reflector.call(myTestClass, "methodWithTwoParams", params6).toString());

		assertEquals("t7", "5", Reflector.call(myTestClass, "anotherMethod", "foo").toString());

		System.out.println("call(Object o, String methodName, Object param1, Object param2)");		

		assertEquals("t8", "foo+bar", Reflector.call(myTestClass, "methodWithTwoParamsAndReturnValue", "foo", "bar").toString());		

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

		public String anotherMethod(String s) {
			return "5";
		}
	}

	public class TestClass2 {

		public TestClass2() {
		}

		@MethodInfo(description="bar", parameters="baz, qux")
		public boolean foo(String baz, Integer qux) {
			return true;
		}

		@MethodInfo(description="bar2", parameters="baz2")
		public String foo2(String baz) {
			return "";
		}
	}


}
