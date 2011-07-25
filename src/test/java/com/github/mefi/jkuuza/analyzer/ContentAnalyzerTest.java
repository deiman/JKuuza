/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.parser.ContentAnalyzer;
import org.jsoup.Jsoup;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mefi
 */
public class ContentAnalyzerTest {

    public ContentAnalyzerTest() {
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
	 * Test of classExists method, of class TestHelpers.
	 */ @Test
	public void testDocHasClass() {
		System.out.println("classExists");

		String html = "";
		String message = "";

		html = "<div><span class=\"foo\"></span></div>";
		message = "expected: true - " + html;
		assertTrue(message, new ContentAnalyzer(Jsoup.parse(html)).docHasClass("foo"));

		html = "<div><span class=\"bar\"></span></div>";
		message = "expected: false - " + html;
		assertFalse(message, new ContentAnalyzer(Jsoup.parse(html)).docHasClass("foo"));
	}

	/**
	 * Test of docHasTag method, of class TestHelpers.
	 */ @Test
	public void testDocHasTag() {
		System.out.println("docHasTag");

		String html = "";
		String message = "";

		html = "<div><span class=\"foo\"></span></div>";
		message = "expected: true - " + html;
		assertTrue(message, new ContentAnalyzer(Jsoup.parse(html)).docHasTag("span"));

		html = "<div><span class=\"foo\"></span></div>";
		message = "expected: false - " + html;
		assertFalse(message, new ContentAnalyzer(Jsoup.parse(html)).docHasTag("h1"));
	}

	/**
	 * Test of tagHasValue method, of class TestHelpers.
	 */ 
/*
	@Test
	public void testTagHasValue() {
		System.out.println("tagHasValue");
		String tagName = "";
		String value = "";
		ContentAnalyzer instance = null;
		boolean expResult = false;
		boolean result = instance.tagHasValue(tagName, value);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
*/
	
}