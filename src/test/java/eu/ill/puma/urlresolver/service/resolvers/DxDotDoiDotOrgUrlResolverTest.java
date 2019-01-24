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

import eu.ill.puma.urlresolver.UrlResolverApplication;
import eu.ill.puma.urlresolver.UrlResolverConfiguration;
import eu.ill.puma.urlresolver.core.ResolverResult;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class DxDotDoiDotOrgUrlResolverTest {

	@ClassRule
	public static final DropwizardAppRule<UrlResolverConfiguration> RULE = new DropwizardAppRule<UrlResolverConfiguration>(UrlResolverApplication.class, ResourceHelpers.resourceFilePath("url-resolver-test.yml"));

	@Test
	public void testResolver() throws Exception {

		String url = "http://dx.doi.org/10.1021/cg5000205";
		String doi = "10.1021/cg5000205";

		DxDotDoiDotOrgRedirectionResolver dxDotDoiDotOrgRedirectionResolver = new DxDotDoiDotOrgRedirectionResolver(url, doi);
		ResolverResult result = dxDotDoiDotOrgRedirectionResolver.resolve();

		String expectedUrl = "http://pubs.acs.org/doi/10.1021/cg5000205";

		Assert.assertEquals(0, result.getResolvedUrls().size());
		Assert.assertNotNull(result.getNextStepUrl());
		Assert.assertEquals(expectedUrl, result.getNextStepUrl());
	}

}
