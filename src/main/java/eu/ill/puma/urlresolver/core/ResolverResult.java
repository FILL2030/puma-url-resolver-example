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

import java.util.ArrayList;
import java.util.List;

public class ResolverResult {

	private List<ResolverUrl> resolvedUrls = new ArrayList<>();
	private List<DownloadData> downloads = new ArrayList<>();
	private String nextStepUrl;
	private String host;

	public ResolverResult(String host, String nextStepUrl) {
		this.nextStepUrl = nextStepUrl;
		this.host = host;
	}

	public ResolverResult(String host) {
		this.host = host;
	}

	public List<ResolverUrl> getResolvedUrls() {
		return resolvedUrls;
	}

	public void addResolvedUrl(ResolverUrl resolvedUrl) {
		this.resolvedUrls.add(resolvedUrl);
	}

	public List<DownloadData> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<DownloadData> downloads) {
		this.downloads = downloads;
	}

	public void addDownload(DownloadData downloadData) {
		downloads.add(downloadData);
	}

	public String getNextStepUrl() {
		return nextStepUrl;
	}

	public void setNextStepUrl(String nextStepUrl) {
		this.nextStepUrl = nextStepUrl;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
