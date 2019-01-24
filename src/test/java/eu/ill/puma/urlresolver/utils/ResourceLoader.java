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
package eu.ill.puma.urlresolver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {

	private final static ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);


	public static byte[] readByteArray(String fileName) {
		URL fileURL = Thread.currentThread().getContextClassLoader().getResource(fileName);
		if (fileURL == null) {
			throw new RuntimeException("Cannot get resource file \"" + fileName +"\"");
		}

		//Handle Windows path (starts with HDD letter but we have to remove /)
		String testFilePath = fileURL.getFile().replaceFirst("^/([CD])", "$1");

		byte[] encoded;
		try {
			 encoded = Files.readAllBytes(Paths.get(testFilePath));

		} catch (IOException e) {
			throw new RuntimeException("Error reading resource file \"" + fileName +"\"");
		}

		log.info("Read resource file \"" + fileName + "\"");
		return encoded;
	}

	public static String readString(String fileName) throws UnsupportedEncodingException {
		return new String(readByteArray(fileName), "UTF-8");
	}

	public static <ResourceType> ResourceType readType(String fileName, Class<ResourceType> resourceTypeClass) throws IOException {
		String resourceString = readString(fileName);

		return objectMapper.readValue(resourceString, resourceTypeClass);
	}
}
