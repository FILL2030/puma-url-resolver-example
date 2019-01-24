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

import eu.ill.puma.urlresolver.health.ApplicationHealthCheck;
import eu.ill.puma.urlresolver.resources.WebserviceEndpoint;
import eu.ill.puma.urlresolver.service.UrlResolver;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class UrlResolverApplication extends Application<UrlResolverConfiguration> {

    public static void main(final String[] args) throws Exception {
        new UrlResolverApplication().run(args);
    }

    @Override
    public String getName() {
        return "Puma Url Resolver";
    }

    @Override
    public void initialize(final Bootstrap<UrlResolverConfiguration> bootstrap) {
    }

    @Override
    public void run(final UrlResolverConfiguration configuration, final Environment environment) {

		// Health checks
		environment.healthChecks().register("base", new ApplicationHealthCheck());

		// The resolver factory
		UrlResolver urlResolver = new UrlResolver();

		// jersey config
        environment.jersey().register(new WebserviceEndpoint(urlResolver));
    }

}
