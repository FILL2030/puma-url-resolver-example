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
package eu.ill.puma.urlresolver.core.response;

import eu.ill.puma.urlresolver.core.DownloadData;
import eu.ill.puma.urlresolver.core.DownloadDataBase64;
import eu.ill.puma.urlresolver.core.ResolverUrl;

import java.util.ArrayList;
import java.util.List;

public class UrlResolverResponse {

	private String originUrl;
	private String doi;
	private String resolverHost;
	private List<ResolverUrl> urls = new ArrayList<ResolverUrl>();
	private List<DownloadDataBase64> downloads = new ArrayList<>();
	private String message;
	private ResponseCode code;

	public UrlResolverResponse() {
	}

	public UrlResolverResponse(String originUrl, String doi) {
		this.originUrl = originUrl;
		this.doi = doi;
	}

	public String getOriginUrl() {
		return originUrl;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getResolverHost() {
		return resolverHost;
	}

	public void setResolverHost(String resolverHost) {
		this.resolverHost = resolverHost;
	}

	public List<ResolverUrl> getUrls() {
		return urls;
	}

	public void addUrl(ResolverUrl resolvedUrl) {
		this.urls.add(resolvedUrl);
	}

	public void setUrls(List<ResolverUrl> urls) {
		this.urls = urls;
	}

	public List<DownloadDataBase64> getDownloads() {
		return downloads;
	}

	public void setDownloadsBase64(List<DownloadDataBase64> downloads) {
		this.downloads = downloads;
	}

	public void setDownloads(List<DownloadData> downloads) {
		for (DownloadData download : downloads) {
			this.downloads.add(new DownloadDataBase64(download));
		}
	}

	public void addDownload(DownloadData downloadData) {
		downloads.add(new DownloadDataBase64(downloadData));
	}

	public void addDownload(DownloadDataBase64 downloadDataBase64) {
		downloads.add(downloadDataBase64);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
}
