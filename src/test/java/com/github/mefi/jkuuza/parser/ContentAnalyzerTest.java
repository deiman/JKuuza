/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.mefi.jkuuza.parser;

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
	 * Test of docHasClass method, of class ContentAnalyzer.
	 */ @Test
	public void testDocHasClass() {
		System.out.println("docHasClass");

		String message = "";
		String html = "";
		ContentAnalyzer analyzer = null;

		html = "<p class=\"foo\">foo</p>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasClass("foo"));

		html = "<p class=foo>foo</p>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasClass("foo"));

		html = "<div class=\"foo\"><span class=\"bar\"><p class=\"baz\">foo</p></span></div>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasClass("baz"));

		html = "<div class=\"foo\"><span class=\"bar\"><p class=\"baz\">foo</p></span></div>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.docHasClass("qar"));
	}

	/**
	 * Test of docHasTag method, of class ContentAnalyzer.
	 */ @Test
	public void testDocHasTag() {
		System.out.println("docHasTag");

		String message = "";
		String html = "";
		ContentAnalyzer analyzer = null;

		html = "<a>foo</a>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasTag("a"));

		html = "<span>foo<p>bar</p></span>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasTag("p"));

		html = "<span>foo<p>bar</p></span>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.docHasTag("img"));
	}

	/**
	 * Test of tagHasValue method, of class ContentAnalyzer.
	 */ @Test
	public void testTagHasValue() {
		System.out.println("tagHasValue");

		String message = "";
		String html = "";
		ContentAnalyzer analyzer = null;

		html = "<a>foo</a>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.tagHasValue("a", "foo"));

		html = "<a>foo</a>";
		message = "expected: false - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.tagHasValue("a", "bar"));

		html = "<span>foo<a>bar</a>baz</span>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.tagHasValue("span", "foobaz"));
	}

	/**
	 * Test of tagHasAttributeWithValue method, of class ContentAnalyzer.
	 */ @Test
	public void testTagHasAttributeWithValue() {
		System.out.println("tagHasAttributeWithValue");

		String message = "";
		String html = "";
		ContentAnalyzer analyzer = null;

		html = "<img src=\"foo.jpg\" alt=\"bar\" />";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.tagHasAttributeWithValue("img", "alt", "bar"));

		html = "<img src=\"foo.jpg\" alt=\"barbaz\" />";
		message = "expected: false - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.tagHasAttributeWithValue("img", "alt", "bar"));

		html = "<img>";
		message = "expected: false - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.tagHasAttributeWithValue("img", "alt", "bar"));

		html = "<img id=\"but_63815_2500_272\" border=\"0\" alt=\"Přidat do košíku\" src=\"/images/buttons/product_catalog_buy_detail.gif\">";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.tagHasAttributeWithValue("img", "alt", "Přidat do košíku"));
	}

	/**
	 * Test of docHasId method, of class ContentAnalyzer.
	 */ @Test
	public void testDocHasId() {
		System.out.println("docHasId");

		String message = "";
		String html = "";
		ContentAnalyzer analyzer = null;

		html = "<div id=\"foo\">bar</div>";
		message = "expected: true - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertTrue(message, analyzer.docHasId("foo"));

		html = "<div id=\"foo\">bar</div>";
		message = "expected: false - " + html;
		analyzer = new ContentAnalyzer(Jsoup.parse(html));
		assertFalse(message, analyzer.docHasId("bar"));
	}

}