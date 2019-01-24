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
import eu.ill.puma.urlresolver.core.DownloadData;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class ByteDownloaderTest {

	@ClassRule
	public static final DropwizardAppRule<UrlResolverConfiguration> RULE = new DropwizardAppRule<UrlResolverConfiguration>(UrlResolverApplication.class, ResourceHelpers.resourceFilePath("url-resolver-test.yml"));

	@Test
	public void testByteDownloadPdf() throws Exception {

		String url = "https://link.springer.com/content/pdf/10.1007%2Fs10853-017-0997-6.pdf";

		DownloadData downloadData = HttpService.getInstance().downloadBytesFromUrl(url);

		Assert.assertNotNull(downloadData);

		// Compare checksum with results from manually downloaded file
		String expectedChecksum = "4f198a4fa5518729f0338781104f8973"; // Generated using Win10's built in certUtil library
		Assert.assertEquals(expectedChecksum.toLowerCase(), downloadData.getMd5Checksum().toLowerCase());

		// Compare expected mtype
		String expectedMType = "application/pdf";
		Assert.assertEquals(expectedMType, downloadData.getMimeType());

	}

}
