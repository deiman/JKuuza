package com.github.mefi.jkuuza.model;

import java.net.SocketTimeoutException;
import org.ektorp.CouchDbConnector;

/**
 *
 * @author Marek Pilecky
 */
public class CrawledPageController {

	private CouchDbConnector connector;
	private PageRepository pageRepository;
	private BodyContentRepository contentRepository;

	public CrawledPageController(CouchDbConnector connector) {
		this.connector = connector;
		this.pageRepository = new PageRepository(connector);
		this.contentRepository = new BodyContentRepository(connector);
	}

	/**
	 * Saves Page into db. If old record already exists in db, is overwriten by new values.
	 *
	 * @param page
	 */
	public void save(Page page) throws SocketTimeoutException {
		try {
			if (pageRepository.contains(page.getId())) {
				page.setRevision(pageRepository.get(page.getId()).getRevision());
				pageRepository.update(page);

			} else {
				page.setId(createId(page));
				pageRepository.add(page);
			}
		} catch (Exception ex) {
			throw new SocketTimeoutException();
		}
	}


	/**
	 * Saves Page and its BodyContent (if is unique) into db. BodyContent _id depends on MD5 hash of content,
	 * so only chagned content is saved. If Page already exists in db, is overwriten by new values.
	 *
	 * @param page
	 * @param content
	 */
	public void save(Page page, BodyContent content) throws SocketTimeoutException {

		save(page);
		String contentId = BodyContent.createId(content.getUrl(), content.getHash());

		try {
			if (!contentRepository.contains(contentId)) {
				content.setId(contentId);
				contentRepository.add(content);
			}
		} catch (Exception ex) {
			throw new SocketTimeoutException();
		}
		
	}
	
	public void delete(Page page) {
	}

	/**
	 * Returns Page id calculated from instance parameters.
	 *
	 * @param page
	 * @return
	 */
	private String createId(Page page) {
                return page.getUrl();
        }
	
}
