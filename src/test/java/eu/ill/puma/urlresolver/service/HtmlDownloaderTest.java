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

import eu.ill.puma.urlresolver.UrlResolverApplication;
import eu.ill.puma.urlresolver.UrlResolverConfiguration;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class HtmlDownloaderTest {

	@ClassRule
	public static final DropwizardAppRule<UrlResolverConfiguration> RULE = new DropwizardAppRule<UrlResolverConfiguration>(UrlResolverApplication.class, ResourceHelpers.resourceFilePath("url-resolver-test.yml"));

	@Test
	public void testHtmlDownload() throws Exception {

		String url = "http://gateway.webofknowledge.com/gateway/Gateway.cgi?GWVersion=2&SrcApp=PARTNER_APP&SrcAuth=LinksAMR&KeyUT=WOS:000333948000042&DestLinkType=FullRecord&DestApp=ALL_WOS&UsrCustomerID=b32b43502a62b17d1dbb1635f87b293b";

		String html = HttpService.getInstance().downloadString(url);

		Assert.assertNotNull(html);
	}

	@Test(expected=UrlResolverFail.class)
	public void test404() throws Exception {

		String url = "https://github.com/dropwizard/dropwizard/tree/master/dropwizard-fds";

		String html = HttpService.getInstance().downloadString(url);

		Assert.assertNotNull(html);
	}
}
