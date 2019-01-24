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
package eu.ill.puma.urlresolver.service;

import eu.ill.puma.urlresolver.core.ResolverResult;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverBusy;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverNotImplemented;
import eu.ill.puma.urlresolver.service.resolvers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class UrlResolver {
	private static final Logger log = LoggerFactory.getLogger(UrlResolver.class);

	private static final String DX_DOI_ORG_MATCH = "dx.doi.org";
	private static final String DOI_ORG_MATCH = "doi.org";
	private static final String LINKING_HUB_ELSEVIER_COM_MATCH = "linkinghub.elsevier.com";
	private static final String SCIENCE_DIRECT_COM_MATCH = "www.sciencedirect.com";

	private static long RESOLVE_ID = 0;

	private synchronized static long getNextResolveId() {
		return ++RESOLVE_ID;
	}

	public AbstractUrlResolver get(String urlString, String doi) throws UrlResolverNotImplemented, MalformedURLException {

		// Verify that the url is valid
		URL url = new URL(urlString);

		// Get host from the URL and match to known resolvers
		String host = url.getHost();
		if (host.matches(DX_DOI_ORG_MATCH) || host.matches(DOI_ORG_MATCH)) {
			return new DxDotDoiDotOrgRedirectionResolver(urlString, doi);

		} else if (host.matches(LINKING_HUB_ELSEVIER_COM_MATCH)) {
			return new ElsevierDotComUrlResolver(urlString, doi);

		} else if (host.matches(SCIENCE_DIRECT_COM_MATCH)) {
			return new ElsevierDotComUrlResolver(urlString, doi);

		} else {
			throw new UrlResolverNotImplemented(url.getHost(), "No resolver exists for " + url.getHost());
		}
	}

	public ResolverResult resolve(String url, String doi) throws UrlResolverNotImplemented, MalformedURLException, UrlResolverFail, UrlResolverError, UrlResolverBusy {

		// Force passage via dx.doi.org if no URL is provided
		if (url == null && doi != null) {
			url = "http://" + DX_DOI_ORG_MATCH + "/" + doi;
			log.info("Resolving via doi.org with URL " + url);

		} else if (url == null && doi == null) {
			throw new UrlResolverFail(null, null, "URL and/or DOI required to resolve download URLs");
		}

		String nextStepUrl = url;
		ResolverResult resolverResult = null;

		// Avoids circular resolving
		Set<String> resolvedUrls = new HashSet<>();

		long resolveId = getNextResolveId();

		// Iterate over resolver results until a final resolved URL is found
		do {
			resolvedUrls.add(nextStepUrl);

			log.debug("Resolving (" + resolveId + ") " + nextStepUrl);

			// Get resolved Url
			resolverResult = this.get(nextStepUrl, doi).resolveWithThrottle();

			// Continue until next step url is null (or repeat detected)
			if (resolverResult.getNextStepUrl() != null) {
				log.debug("Resolved next step (" + resolveId + ") " + resolverResult.getNextStepUrl() + " from " + nextStepUrl);

			} else {
				log.debug("Resolve terminated for " + nextStepUrl);
			}

			// update next step URL
			nextStepUrl = resolverResult.getNextStepUrl();

		} while (nextStepUrl != null && !resolvedUrls.contains(nextStepUrl));

		return resolverResult;
	}

}
