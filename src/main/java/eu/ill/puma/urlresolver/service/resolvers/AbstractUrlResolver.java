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
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverBusy;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.utils.Throttle;
import eu.ill.puma.urlresolver.utils.ThrottleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractUrlResolver {

	private static final Logger log = LoggerFactory.getLogger(AbstractUrlResolver.class);

	protected String originUrl;
	protected String host;
	protected String doi;
	protected String protocol;

	private Integer throttleTimeMillis = null;

	public AbstractUrlResolver(String originUrl, String doi) {
		this.originUrl = originUrl;
		this.doi = doi;

		try {
			URL url = new URL(originUrl);
			this.host = url.getHost();
			this.protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			log.warn("Resolver contains a malformed URL : " + this.originUrl);
		}
	}

	public AbstractUrlResolver(String originUrl, String doi, int throttleTimeMillis) {
		this.originUrl = originUrl;
		this.doi = doi;
		this.throttleTimeMillis = throttleTimeMillis;

		try {
			URL url = new URL(originUrl);
			this.host = url.getHost();
			this.protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			log.warn("Resolver contains a malformed URL : " + this.originUrl);
		}
	}

	public ResolverResult resolveWithThrottle() throws UrlResolverFail, UrlResolverError, MalformedURLException {

		// Throttle for this host
		ThrottleStore.getInstance().get(this.host).throttle(this.throttleTimeMillis);

		return this.resolve();
	}

	public ResolverResult resolveIfNotBusy() throws UrlResolverFail, UrlResolverError, UrlResolverBusy, MalformedURLException {

		// Throttle for this host
		Throttle throttle = ThrottleStore.getInstance().get(host);
		if (throttle.throttleOrBusy(this.throttleTimeMillis)) {

			// Resolve
			return this.resolve();

		} else {
			throw new UrlResolverBusy(this.host, this.originUrl);
		}
	}

	public abstract ResolverResult resolve() throws UrlResolverFail, UrlResolverError;

}
