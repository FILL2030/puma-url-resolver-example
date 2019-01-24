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
package eu.ill.puma.urlresolver.resources;

import eu.ill.puma.urlresolver.core.ResolverResult;
import eu.ill.puma.urlresolver.core.response.UrlResolverResponse;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverBusy;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverNotImplemented;
import eu.ill.puma.urlresolver.utils.MD5Checksum;
import eu.ill.puma.urlresolver.utils.UrlResolverResponseBuilder;
import eu.ill.puma.urlresolver.service.UrlResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WebserviceEndpoint {

	private static final Logger log = LoggerFactory.getLogger(WebserviceEndpoint.class);

	private UrlResolver urlResolver;

	public WebserviceEndpoint(UrlResolver urlResolver) {
		this.urlResolver = urlResolver;
	}

	@GET
	public Response resolveUrl(@QueryParam("url") String urlString, @QueryParam("doi") String doi) {

		String check = urlString != null ? Integer.toHexString(urlString.hashCode()) + "" : doi != null ? Integer.toHexString(doi.hashCode()) + "" : "";
		try {
			check = MD5Checksum.getMD5Checksum(urlString != null ? urlString : doi);
		} catch (Exception e) {
			// ignore
		}

		if (urlString != null) {
			log.info("Resolving for URL (" + check + ") :" + urlString);

		} else if (doi != null) {
			log.info("Resolving for DOI (" + check + ") :" + doi);
		}

		String idString = urlString != null ? urlString : doi;

		UrlResolverResponse response = null;
		try {
			ResolverResult resolverResult = this.urlResolver.resolve(urlString, doi);
			if (resolverResult.getResolvedUrls().size() > 0 || resolverResult.getDownloads().size() > 0) {
				log.info("Resolved URL (" + check + ") :" + idString);

				response = UrlResolverResponseBuilder.success(urlString, doi, resolverResult);

			} else {
				log.warn("No resolver for (" + check + ") :" + idString);

				URL unresolvedHostUrl = new URL(resolverResult.getNextStepUrl());

				// Create response with the URL that wasn't supported (possibly after redirection)
				response = UrlResolverResponseBuilder.notSupported(urlString, doi, unresolvedHostUrl.getHost());
			}

		} catch (MalformedURLException e) {
			log.warn("Received request with malformed URL  (" + check + ") :" + idString);
			response = UrlResolverResponseBuilder.fail(urlString, doi, null, "Malformed URL");

		} catch (UrlResolverNotImplemented notImplemented) {
			log.warn("No resolver for (" + check + ") :" + notImplemented.getHost() + " while resolving : " + idString);
			response = UrlResolverResponseBuilder.notSupported(urlString, doi, notImplemented.getHost());

		} catch (UrlResolverFail fail) {
			log.warn("Resolver failed for (" + check + ") :" + fail.getHost() + " (" + fail.getMessage() + ") while resolving : " + idString);
			response = UrlResolverResponseBuilder.fail(urlString, doi, fail.getHost(), fail.getMessage());

		} catch (UrlResolverBusy busy) {
			log.warn("Resolver busy for (" + check + ") :" + busy.getHost() + ") while resolving : " + idString);
			response = UrlResolverResponseBuilder.busy(urlString, doi, busy.getHost());

		} catch (UrlResolverError error) {
			log.warn("Resolver error for (" + check + ") :" + error.getHost() + " (" + error.getMessage() + ") while resolving : " + idString);
			response = UrlResolverResponseBuilder.error(urlString, doi, error.getHost(), error.getMessage());
		}

		return Response.ok(response).build();
	}

	@GET
	@Path("/health")
	public Response health() {
		log.info("Requested health check");
		return Response.ok().build();
	}

}