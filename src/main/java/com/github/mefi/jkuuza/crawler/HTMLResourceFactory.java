package com.github.mefi.jkuuza.crawler;

import org.niocchi.core.Resource;
import org.niocchi.core.ResourceFactoryInt;

/**
 *
 * @author Marek Pilecky
 */
class HTMLResourceFactory implements ResourceFactoryInt {

	public Resource createResource() {
		return new HTMLResource();
	}

}
