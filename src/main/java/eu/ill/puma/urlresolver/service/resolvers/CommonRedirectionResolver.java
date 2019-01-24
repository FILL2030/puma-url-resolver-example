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
package eu.ill.puma.urlresolver.service.resolvers;

import eu.ill.puma.urlresolver.core.ResolverResult;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonRedirectionResolver extends AbstractUrlResolver {

	private static final Logger log = LoggerFactory.getLogger(CommonRedirectionResolver.class);

	public CommonRedirectionResolver(String url, String doi) {
		super(url, doi);
	}

	public CommonRedirectionResolver(String url, String doi, int throttleTimeMillis) {
		super(url, doi, throttleTimeMillis);
	}

	@Override
	public ResolverResult resolve() throws UrlResolverError, UrlResolverFail {
		// Get the redirection URL
		String redirectionUrl = HttpService.getInstance().getRedirectUrl(this.originUrl);

		// Return result indicating that the redirection needs to be followed
		return new ResolverResult(this.host, redirectionUrl);
	}
}