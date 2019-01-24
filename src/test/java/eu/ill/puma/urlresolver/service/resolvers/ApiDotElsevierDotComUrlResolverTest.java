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
import eu.ill.puma.urlresolver.core.ResolverUrlFileType;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ApiDotElsevierDotComUrlResolverTest {

	@ClassRule
	public static final DropwizardAppRule<UrlResolverConfiguration> RULE = new DropwizardAppRule<UrlResolverConfiguration>(UrlResolverApplication.class, ResourceHelpers.resourceFilePath("url-resolver-test.yml"));

	@Test
	public void testResolver() throws Exception {

		String elsevierApiKey = UrlResolverConfiguration.getInstance().getElsevierApiKey();

		String url = "http://www.sciencedirect.com/science/article/pii/S0168900209015113";
		String doi = "10.1016/j.nima.2009.07.046";

		ElsevierDotComUrlResolver resolver = new ElsevierDotComUrlResolver(url, doi);
		ResolverResult result = resolver.resolve();

		String expectedPdfUrl = "https://api.elsevier.com/content/article/doi/10.1016/j.nima.2009.07.046?apiKey=" + elsevierApiKey + "&httpAccept=application%2Fpdf";
		String expectedXmlUrl = "https://api.elsevier.com/content/article/doi/10.1016/j.nima.2009.07.046?apiKey=" + elsevierApiKey + "&httpAccept=text%2Fxml";

		Assert.assertEquals(6, result.getResolvedUrls().size());
		Assert.assertEquals(expectedPdfUrl, result.getResolvedUrls().get(0).getUrl());
		Assert.assertEquals(expectedXmlUrl, result.getResolvedUrls().get(1).getUrl());
		Assert.assertEquals(ResolverUrlFileType.EXTRACTED_IMAGE, result.getResolvedUrls().get(3).getFileType());
	}


	@Test
	public void testLowResolutionImageExtraction() throws Exception {

		String url = "http://www.sciencedirect.com/science/article/pii/S0301462210000311";
		String doi = "10.1016/j.bpc.2010.02.003";

		ElsevierDotComUrlResolver resolver = new ElsevierDotComUrlResolver(url, doi);
		ResolverResult result = resolver.resolve();

		Assert.assertEquals(9, result.getResolvedUrls().size());
		Assert.assertEquals(ResolverUrlFileType.EXTRACTED_IMAGE, result.getResolvedUrls().get(3).getFileType());
	}

	@Test
	public void testNoImages() throws Exception {

		String elsevierApiKey = UrlResolverConfiguration.getInstance().getElsevierApiKey();

		String url = "http://www.sciencedirect.com/science/article/pii/0168900294901597";
		String doi = "10.1016/0168-9002(94)90159-7";

		ElsevierDotComUrlResolver resolver = new ElsevierDotComUrlResolver(url, doi);
		ResolverResult result = resolver.resolve();

		String expectedPdfUrl = "https://api.elsevier.com/content/article/doi/10.1016/0168-9002(94)90159-7?apiKey=" + elsevierApiKey + "&httpAccept=application%2Fpdf";

		Assert.assertEquals(2, result.getResolvedUrls().size());
		Assert.assertEquals(expectedPdfUrl, result.getResolvedUrls().get(0).getUrl());
	}

}