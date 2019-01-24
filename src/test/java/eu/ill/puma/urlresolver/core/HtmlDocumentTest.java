/*
 * Copyright 2019 Institut Laue–Langevin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.ill.puma.urlresolver.core;

import eu.ill.puma.urlresolver.utils.ResourceLoader;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class HtmlDocumentTest {

	@Test
	public void testGetButtonElements() throws Exception {

		String html = ResourceLoader.readString("testFile.html");

		Assert.assertNotNull(html);

		Elements elements = HtmlDocument.parse(html).getElements(".button9");

		Assert.assertTrue(elements.size() > 0);
	}

	@Test
	public void testGetButtonTextElements() throws Exception {

		String html = ResourceLoader.readString("testFile.html");

		Assert.assertNotNull(html);

		Elements elements = HtmlDocument.parse(html).getElements(".button9.full_text_link");

		Assert.assertTrue(elements.size() > 0);
	}

	@Test
	public void testGetUrlElement() throws Exception {

		String html = ResourceLoader.readString("testFile.html");

		Assert.assertNotNull(html);

		Element element = HtmlDocument.parse(html).getElement(".button9.full_text_link.DOISOURCE_link");

		Assert.assertNotNull(element);
	}

}
