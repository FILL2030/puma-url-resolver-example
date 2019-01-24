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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import eu.ill.puma.urlresolver.core.DownloadData;
import eu.ill.puma.urlresolver.core.ResolverUrl;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.utils.MD5Checksum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpService {

	private static final Logger log = LoggerFactory.getLogger(HttpService.class);


	private static HttpService instance = null;

	static int MAX_RETRY = 3;

	private HttpService() {
	}

	public static HttpService getInstance() {
		if (instance == null) {
			instance = new HttpService();
		}

		return instance;
	}

	public <ResourceType> ResourceType downloadObject(String url, Class<ResourceType> clazz) throws UrlResolverError, UrlResolverFail {
		String rawData = this.downloadString(url);

		XmlMapper xmlMapper = new XmlMapper();
		try {
			ResourceType resource = xmlMapper.readValue(rawData, clazz);

			return resource;

		} catch (IOException e) {
			throw new UrlResolverFail(this.getHost(url), url, "Failed to convert XML data from " + url, e);
		}
	}

	public String downloadString(String url) throws UrlResolverError, UrlResolverFail {
		RequestConfig requestConfig = this.buildConfig(true);

		CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();

		HttpGet request = new HttpGet(url);

		request.setConfig(requestConfig);

		try {
			// Execute request
			CloseableHttpResponse response = httpClient.execute(request);

			StatusLine status = response.getStatusLine();

			// Test response code
			if (status.getStatusCode() >= 500) {
				throw new UrlResolverError(this.getHost(url), url, "Server error " + status.getStatusCode());

			} else if (status.getStatusCode() >= 400) {
				throw new UrlResolverFail(this.getHost(url), url, "Client error " + status.getStatusCode());
			}

			// Convert response to string
			String html = EntityUtils.toString(response.getEntity());

			return html;

		} catch (IOException e) {
			throw new UrlResolverError(this.getHost(url), url, "Exception occurred during html downloadString at " + url, e);

		} finally {

			try {
				// Close client
				httpClient.close();
			} catch (IOException ioe) {
				// log as warning
				log.warn("Error closing http client after request to " + url, ioe);
			}

		}
	}

	public String getRedirectUrl(String url) throws UrlResolverError, UrlResolverFail {
		RequestConfig requestConfig = this.buildConfig(false);

		CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();

		HttpGet request = new HttpGet(url);

		request.setConfig(requestConfig);

		try {
			int retry = 0;
			boolean isOk = false;
			CloseableHttpResponse response = null;
			while (retry < MAX_RETRY && !isOk) {

				// Execute request
				response = httpClient.execute(request);

				StatusLine status = response.getStatusLine();

				// Test response code
				if (status.getStatusCode() < 300 || status.getStatusCode() >= 400) {
					// Convert response to string
//					String html = EntityUtils.toString(response.getEntity());

					// Retry because sometimes elsevier produces a page with JS instead of a redirect.
					retry++;

				} else {
					isOk = true;
				}
			}

			if (retry == MAX_RETRY) {
				throw new UrlResolverFail(this.getHost(url), url, "Server did not return an expected redirection for URL " + url);
			}

			// Get redirection url
			String redirectURL = response.getFirstHeader("Location").getValue();

			return redirectURL;

		} catch (IOException e) {
			throw new UrlResolverError(this.getHost(url), url, "Exception occurred when obtaining redirection URL from  " + url, e);

		} finally {

			try {
				// Close client
				httpClient.close();
			} catch (IOException ioe) {
				// log as warning
				log.warn("Error closing http client after request to " + url, ioe);
			}

		}
	}


	public DownloadData downloadBytesFromUrl(String url) {
		return downloadBytesFromUrl(url, null);
	}

	/**
	 * Downloads the contents of a given url as a byte array.
	 * Places the byte array in a DownloadData object along with calculated MType and Md5 Checksum
	 * @param url The address to be downloaded. Must be direct.
	 * @return The DownloadData object, ready to be passed on to the resolver.
	 */
	public DownloadData downloadBytesFromUrl(String url, List<Header> headers) {
		// Set up client
		CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
		// Create http request
		RequestConfig requestConfig = RequestConfig.DEFAULT;
		HttpGet request = new HttpGet(url);
		request.setConfig(requestConfig);

		if (headers != null) {
			for (Header header : headers) {
				request.setHeader(header);
			}
		}
		// Execute request
		try {
			CloseableHttpResponse response = httpClient.execute(request);
			InputStream inputStream = response.getEntity().getContent();
			// Create Download data object and assign byte array
			DownloadData downloadData = new DownloadData(IOUtils.toByteArray(inputStream));
			// Use md5checksum util to calculate checksum
			downloadData.setMd5Checksum(MD5Checksum.getMD5Checksum(downloadData.getData()));
			// Get mtype
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header header = entity.getContentType();
				if (header == null) {
					header = response.getFirstHeader("Content-Type");
				}
				if (header != null) {
					String mimeTypes = header.getValue();
					String[] allMimeTypes = mimeTypes.split(";");
					downloadData.setMimeType(allMimeTypes[0]);
				} else {
					if (downloadData.isPdf()) {
						downloadData.setMimeType("application/pdf");
					}
				}
			}

			return downloadData;



		} catch (IOException exception) {
			log.error("Get request to " + url + " failed.", exception);
		} finally {
			try {
				httpClient.close();
			} catch (Exception e) {
				log.warn("Error closing http client after request to " + url, e);
			}
		}
		return null;
	}

	public DownloadData downloadBytesFromUrl(ResolverUrl resolverUrl) {
		return downloadBytesFromUrl(resolverUrl.getUrl());
	}

	public String downloadStringToFile(String url, String filePath) {
		try {
			String html = this.downloadString(url);

			File file = new File(filePath);

			// Write file
			FileUtils.writeStringToFile(file, html, Charset.defaultCharset());

			log.info("File saved at " + file.getAbsolutePath());

			return html;
		} catch (Exception e) {
			log.error("Could not downloadString html from " + url, e);
		}

		return null;
	}


	public Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<>();
		String query = url.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

	private RequestConfig buildConfig(boolean allowRedirect) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.STANDARD)
				.setRedirectsEnabled(allowRedirect)
				.setCircularRedirectsAllowed(true)
				.build();

		return requestConfig;
	}

	public String getHost(String urlString) {
		try {
			URL url = new URL(urlString);

			return url.getHost();

		} catch (MalformedURLException e) {
			return urlString;
		}
	}

}
