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

import eu.ill.puma.urlresolver.UrlResolverConfiguration;
import eu.ill.puma.urlresolver.core.ResolverResult;
import eu.ill.puma.urlresolver.core.ResolverUrl;
import eu.ill.puma.urlresolver.core.ResolverUrlFileType;
import eu.ill.puma.urlresolver.core.elsevier.ElsevierObject;
import eu.ill.puma.urlresolver.core.elsevier.ElsevierObjectCollection;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverError;
import eu.ill.puma.urlresolver.core.exceptions.UrlResolverFail;
import eu.ill.puma.urlresolver.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ElsevierDotComUrlResolver extends AbstractUrlResolver {

    private static final Logger log = LoggerFactory.getLogger(ElsevierDotComUrlResolver.class);
    private static final String FULL_TEXT_API_URL = "https://api.elsevier.com/content/article/doi/";
    private static final String OBJECT_RETRIEVAL_API_URL = "https://api.elsevier.com/content/object/doi/";

    public ElsevierDotComUrlResolver(String url, String doi) {
        super(url, doi, 0);
    }

    @Override
    public ResolverResult resolve() throws UrlResolverError, UrlResolverFail {
        String elsevierApiKey = UrlResolverConfiguration.getInstance().getElsevierApiKey();

        if (this.doi == null) {
            throw new UrlResolverFail(this.host, this.originUrl, "DOI is required to obtain full text from Elsevier");

        } else {
            // Get the redirection URL
            String resolvedFirstPagePDFUrl = FULL_TEXT_API_URL + this.doi + "?apiKey=" + elsevierApiKey + "&httpAccept=application%2Fpdf";
            String resolvedFullTextXmlUrl = FULL_TEXT_API_URL + this.doi + "?apiKey=" + elsevierApiKey + "&httpAccept=text%2Fxml";

            // Return result with resolved Urls for both PDF first page and HTML and XML full text version
            ResolverResult resolverResult = new ResolverResult(this.host);
            resolverResult.addResolvedUrl(new ResolverUrl(resolvedFirstPagePDFUrl, ResolverUrlFileType.PUBLICATION_FIRST_PAGE));
            resolverResult.addResolvedUrl(new ResolverUrl(resolvedFullTextXmlUrl, ResolverUrlFileType.PUBLICATION));

            // Make call to elsevier API to get all image URLs
            List<String> urls = this.resolveImageUrls();
            for (String url : urls) {
                resolverResult.addResolvedUrl(new ResolverUrl(url, ResolverUrlFileType.EXTRACTED_IMAGE));
            }

            return resolverResult;
        }
    }

    private List<String> resolveImageUrls() throws UrlResolverError, UrlResolverFail {
        List<String> urls = new ArrayList<>();

        String elsevierApiKey = UrlResolverConfiguration.getInstance().getElsevierApiKey();
        String articleObjectsUrl = OBJECT_RETRIEVAL_API_URL + this.doi + "?apiKey=" + elsevierApiKey + "&view=STANDARD";

        // Get deserialized object
        try {
            ElsevierObjectCollection collection = HttpService.getInstance().downloadObject(articleObjectsUrl, ElsevierObjectCollection.class);

            // Get all relevant image URLs
            List<ElsevierObject> objects = collection.getObjects();
            Map<String, Map<String, String>> allImages = new HashMap<>();

            for (ElsevierObject object : objects) {
            	String imageRef = object.getRef();
            	String imageType = object.getType();

                if (imageType.equals("IMAGE-HIGH-RES") || imageType.equals("IMAGE-DOWNSAMPLED") || imageType.equals("IMAGE-THUMBNAIL")) {
                	Map<String, String> imageQualities = allImages.get(imageRef);
                	if (imageQualities == null) {
                		imageQualities = new HashMap<>();
                		allImages.put(imageRef, imageQualities);
					}

					String imageUrl = object.getUrl() + "&apiKey=" + elsevierApiKey;
					imageQualities.put(imageType, imageUrl);
                }
            }

            // Get best quality image URL to download
			for (String imageRef : allImages.keySet()) {
            	Map<String, String> imageQualities = allImages.get(imageRef);
				if (imageQualities.containsKey("IMAGE-HIGH-RES")) {
					urls.add(imageQualities.get("IMAGE-HIGH-RES"));

				} else if (imageQualities.containsKey("IMAGE-DOWNSAMPLED")) {
					urls.add(imageQualities.get("IMAGE-DOWNSAMPLED"));

				} else if (imageQualities.containsKey("IMAGE-THUMBNAIL")) {
					urls.add(imageQualities.get("IMAGE-THUMBNAIL"));
				}
			}

			// sort URLs
			Collections.sort(urls);

        } catch (UrlResolverFail fail) {
            log.warn("Failed to get Image URLs for " + articleObjectsUrl, fail);
        }

        return urls;
    }
}
