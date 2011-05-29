package com.github.mefi.jkuuza.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

/**
 *
 * @author mefi
 */
public class LinksExtractorTest {

	public LinksExtractorTest() {
	}
	Document doc = null;
	LinksExtractor extractor = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		doc = new Document("http://example.com");
		extractor = new LinksExtractor(doc);
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of getInternalLinks method, of class LinksExtractor.
	 */
	@Test
	public void testGetInternalLinks() {

		System.out.println("getInternalLinks");

		String html = "";
		String host = "";
		String expectedUrl = "";


		html = "<a href=\"http://example.cz\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"http://example.cz\"></a>";
		host = "example.cz";
		expectedUrl = "http://example.cz";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"/foo\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz/foo";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"foo/\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz/foo/";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));
/**
		html = "<a href=\"../baz\">link</a>";
		host = "example.cz/foo/bar";
		expectedUrl = "http://example.cz/foo/baz";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));
**/
		html = "<a href=\"foo.php?bar=baz\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz/foo.php?bar=baz";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"foo.htm#bar\">link</a>";
		host = "example.cz";
		expectedUrl = "http://example.cz/foo.htm";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));

		html = "<a href=\"foo\">link";
		host = "example.cz";
		expectedUrl = "http://example.cz/foo";
		setDoc(html, host);
		assertThat(extractor.getInternalLinks(host), hasItem(expectedUrl));
	}

	/**
	 * Test of canonizeHost method, of class LinksExtractor.
	 */
	@Test
	public void testCanonizeHost() {
		System.out.println("canonizeHost");

		assertEquals("example.com", extractor.canonizeHost("example.com"));
		assertEquals("example.com", extractor.canonizeHost("www.example.com"));
		assertEquals("example.com", extractor.canonizeHost("http://example.com"));
		assertEquals("example.com", extractor.canonizeHost("https://example.com"));
		assertEquals("example.com", extractor.canonizeHost("http://www.example.com"));
		assertEquals("example.com", extractor.canonizeHost("www.example.com"));
		assertEquals("foo.example.com", extractor.canonizeHost("foo.example.com"));
		assertEquals("foo.example.com", extractor.canonizeHost("http://foo.example.com"));
	}

	/**
	 * Test of createLinkUrl method, of class LinksExtractor.
	 */
	@Test
	public void testCreateLinkUrl() {

		System.out.println("createLinkUrl");

		assertEquals("http://example.com", extractor.createLinkUrl(doc.createElement("a").attr("href", "")));
		assertEquals("http://example.com/foo.htm", extractor.createLinkUrl(doc.createElement("a").attr("href", "http://example.com/foo.htm")));
		assertEquals("http://example.com/foo.htm", extractor.createLinkUrl(doc.createElement("a").attr("href", "foo.htm")));
		assertEquals("http://example.com/foo.htm", extractor.createLinkUrl(doc.createElement("a").attr("href", "./foo.htm")));
		assertEquals("http://example.com/foo.htm", extractor.createLinkUrl(doc.createElement("a").attr("href", "/foo.htm")));
		assertEquals("http://example.com/foo.htm", extractor.createLinkUrl(doc.createElement("a").attr("href", "/foo.htm#anchor")));
		assertEquals("http://example.com/foo/bar/", extractor.createLinkUrl(doc.createElement("a").attr("href", "/foo/bar/")));

		doc.setBaseUri("http://example.com/foo/");
		assertEquals("http://example.com/bar/baz/", extractor.createLinkUrl(doc.createElement("a").attr("href", "../bar/baz/")));

	}

	/**
	 * Test of isInternal method, of class LinksExtractor.
	 */
	@Test
	public void testIsInternal() {
		System.out.println("isInternal");
		String link = "";
		String host = "";


		host = "example.com";
		link = "http://example.com";
		assertTrue(link + " - " + host, extractor.isInternal(link, host));

		host = "example.com";
		link = "http://example.com/bar";
		assertTrue(link + " - " + host, extractor.isInternal(link, host));

		host = "example.com";
		link = "http://foo.example.com";
		assertTrue(link + " - " + host, extractor.isInternal(link, host));

		host = "example.com";
		link = "http://foo.example.com/bar";
		assertTrue(link + " - " + host, extractor.isInternal(link, host));

		host = "foo.example.com";
		link = "http://example.com";
		assertFalse(link + " - " + host, extractor.isInternal(link, host));

		host = "foo.example.com";
		link = "http://bar.example.com";
		assertFalse(link + " - " + host, extractor.isInternal(link, host));

		host = "foo.example.com";
		link = "http://example.com/bar";
		assertFalse(link + " - " + host, extractor.isInternal(link, host));

	}

	private void setDoc(String html, String host) {
		String htmlSkeleton = "<html><head></head><body></body></html>";
		this.doc = Jsoup.parse(htmlSkeleton, "http://" + host);
		this.doc.body().append(html);		
		this.doc.normalise();
		this.extractor = new LinksExtractor(doc);
	}
}
