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
package eu.ill.puma.urlresolver.utils;

import eu.ill.puma.urlresolver.core.ResolverResult;
import eu.ill.puma.urlresolver.core.response.ResponseCode;
import eu.ill.puma.urlresolver.core.response.UrlResolverResponse;

public class UrlResolverResponseBuilder {

	public static UrlResolverResponse success(String originUrl, String doi, ResolverResult resolverResult) {
		UrlResolverResponse response = new UrlResolverResponse(originUrl, doi);
		response.setCode(ResponseCode.SUCCESS);
		response.setUrls(resolverResult.getResolvedUrls());
		response.setResolverHost(resolverResult.getHost());
		response.setDownloads(resolverResult.getDownloads());

		return response;
	}

	public static UrlResolverResponse fail(String originUrl, String doi, String resolverHost, String message) {
		UrlResolverResponse response = new UrlResolverResponse(originUrl, doi);
		response.setCode(ResponseCode.NON_RECOVERABLE_ERROR);
		response.setMessage(message);
		response.setResolverHost(resolverHost);

		return response;
	}

	public static UrlResolverResponse busy(String originUrl, String doi, String resolverHost) {
		UrlResolverResponse response = new UrlResolverResponse(originUrl, doi);
		response.setCode(ResponseCode.BUSY);
		response.setResolverHost(resolverHost);

		return response;
	}

	public static UrlResolverResponse error(String originUrl, String doi, String resolverHost, String message) {
		UrlResolverResponse response = new UrlResolverResponse(originUrl, doi);
		response.setCode(ResponseCode.TRANSIENT_ERROR);
		response.setMessage(message);
		response.setResolverHost(resolverHost);

		return response;
	}

	public static UrlResolverResponse notSupported(String originUrl, String doi, String resolverHost) {
		UrlResolverResponse response = new UrlResolverResponse(originUrl, doi);
		response.setCode(ResponseCode.NOT_SUPPORTED);
		response.setResolverHost(resolverHost);

		return response;
	}

}
