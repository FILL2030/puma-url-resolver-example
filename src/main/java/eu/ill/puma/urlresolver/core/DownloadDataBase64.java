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

import java.util.Base64;

public class DownloadDataBase64 {

	private String data;
	private String md5Checksum;
	private String mimeType;
	private ResolverUrl url;

	public DownloadDataBase64(DownloadData downloadData) {
		this.data = Base64.getEncoder().encodeToString(downloadData.getData());
		this.md5Checksum = downloadData.getMd5Checksum();
		this.mimeType = downloadData.getMimeType();
		this.url = downloadData.getUrl();
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMd5Checksum() {
		return md5Checksum;
	}

	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mType) {
		this.mimeType = mType;
	}

	public ResolverUrl getUrl() {
		return url;
	}

	public void setUrl(ResolverUrl url) {
		this.url = url;
	}
}
