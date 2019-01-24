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

import eu.ill.puma.urlresolver.core.exceptions.HtmlDocumentParseError;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlDocument {

	private Document document;

	public static HtmlDocument parse(String html) {
		HtmlDocument document = new HtmlDocument();

		document.document = Jsoup.parse(html);

		return document;
	}

	public Elements getElements(String query) throws HtmlDocumentParseError {
		return this.document.select(query);
	}

	public Element getElement(String query) throws HtmlDocumentParseError {
		Elements elements = this.document.select(query);

		if (elements.size() == 0) {
			throw new HtmlDocumentParseError(String.format("Expected page for query %s element does not exist.", query));

		} else if (elements.size() > 1) {
			throw new HtmlDocumentParseError(String.format("Page contains multiple elements that fit the query %s", query));
		}

		return elements.get(0);
	}


}
