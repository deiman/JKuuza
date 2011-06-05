package com.github.mefi.jkuuza.parser;

import org.jsoup.Jsoup;
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
public class ContentExtractorTest {

	Document doc = null;
	ContentExtractor extractor = null;

	public ContentExtractorTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		String htmlSkeleton = "<html><head></head><body></body></html>";
		String baseUri = "http://example.com";
		doc = Jsoup.parse(htmlSkeleton, baseUri);
		extractor = new ContentExtractor(doc);
	}

	/**
	 * Test of getMetaDescription method, of class ContentExtractor.
	 */
	@Test
	public void testGetMetaDescription() {
		System.out.println("getMetaDescription");

		String html = "";

		html = "<meta name=\"description\" content=\"foo\" />";
		setDocHeader(html);
		assertEquals("foo", extractor.getMetaDescription());

		html = "<meta name=description content=foo />";
		setDocHeader(html);
		assertEquals("foo", extractor.getMetaDescription());

	}

	/**
	 * Test of getMetaKeywords method, of class ContentExtractor.
	 */
	@Test
	public void testGetMetaKeywords() {
		System.out.println("getMetaKeywords");

		String html = "";

		html = "<meta name=\"keywords\" content=\"foo\" />";
		setDocHeader(html);
		assertEquals("foo", extractor.getMetaKeywords());

		html = "<meta name=\"keywords\" content=\"foo, bar, baz\" />";
		setDocHeader(html);
		assertEquals("foo, bar, baz", extractor.getMetaKeywords());

		html = "<meta name=keywords content=foo />";
		setDocHeader(html);
		assertEquals("foo", extractor.getMetaKeywords());
	}

	/**
	 * Test of getMetaCharset method, of class ContentExtractor.
	 */
	@Test
	public void testGetMetaCharset() {
		System.out.println("getMetaCharset");

		String html = "";

		html = "<meta http-equiv=\"Content-type\" content=\"text/html; charset=UTF-8\">";
		setDocHeader(html);
		assertEquals("UTF-8", extractor.getMetaCharset());

		html = "<META HTTP-EQUIV=\"Content-type\" CONTENT=\"text/html; charset=UTF-8\">";
		setDocHeader(html);
		assertEquals("UTF-8", extractor.getMetaCharset());

		html = "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">";
		setDocHeader(html);
		assertEquals("utf-8", extractor.getMetaCharset());
	}

	/**
	 * Test of hasMetaDescription method, of class ContentExtractor.
	 */
	@Test
	public void testHasMetaDescription() {
		System.out.println("hasMetaDescription");

		String html = "";
		String message = "";

		html = "<meta name=\"description\" content=\"foo\" />";
		message = "expected: true - " + html;
		setDocHeader(html);
		assertTrue(message, extractor.hasMetaDescription());

		html = "";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaDescription());

		html = "<meta name=\"description\" />";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaDescription());
	}

	/**
	 * Test of hasMetaKeywords method, of class ContentExtractor.
	 */
	@Test
	public void testHasMetaKeywords() {
		System.out.println("hasMetaKeywords");

		String html = "";
		String message = "";

		html = "<meta name=\"keywords\" content=\"foo\" />";
		message = "expected: true - " + html;
		setDocHeader(html);
		assertTrue(message, extractor.hasMetaKeywords());

		html = "";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaKeywords());

		html = "<meta name=\"keywords\" />";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaKeywords());
	}

	/**
	 * Test of hasMetaCharset method, of class ContentExtractor.
	 */
	@Test
	public void testHasMetaCharset() {
		System.out.println("hasMetaCharset");

		String html = "";
		String message = "";


		html = "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		message = "expected: true - " + html;
		setDocHeader(html);
		assertTrue(message, extractor.hasMetaCharset());

		html = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
		message = "expected: true - " + html;
		setDocHeader(html);
		assertTrue(message, extractor.hasMetaCharset());

		html = "";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaCharset());

		html = "<META http-equiv=\"Content-Type\" />";
		message = "expected: false - " + html;
		setDocHeader(html);
		assertFalse(message, extractor.hasMetaCharset());
	}

	private void setDocHeader(String html) {
		String htmlSkeleton = "<html><head></head><body></body></html>";
		String baseUri = "http://example.com";
		doc = Jsoup.parse(htmlSkeleton, baseUri);
		this.doc.head().append(html);
		this.doc.normalise();
		this.extractor = new ContentExtractor(doc);

	}

	/**
	 * Test of getTitle method, of class ContentExtractor.
	 */ @Test
	public void testGetTitle() {
		System.out.println("getTitle");

		String html = "";

		html = "<title>foo</title>";
		setDocHeader(html);
		assertEquals("foo", extractor.getTitle());

		html = "<title></title>";
		setDocHeader(html);
		assertEquals("", extractor.getTitle());

		html = "";
		setDocHeader(html);
		String h = doc.html();
		assertEquals("", extractor.getTitle());
	}
}
