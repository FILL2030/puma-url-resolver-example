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
package eu.ill.puma.urlresolver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class UrlResolverConfiguration extends Configuration {

	private static UrlResolverConfiguration instance = null;

	public UrlResolverConfiguration() {
		if (instance == null) {
			instance = this;
		}
	}

	public static UrlResolverConfiguration getInstance() {
		return instance;
	}

	private String elsevierApiKey;

	@JsonProperty
	public String getElsevierApiKey() {
		return elsevierApiKey;
	}

	@JsonProperty
	public void setElsevierApiKey(String elsevierApiKey) {
		this.elsevierApiKey = elsevierApiKey;
	}
}
