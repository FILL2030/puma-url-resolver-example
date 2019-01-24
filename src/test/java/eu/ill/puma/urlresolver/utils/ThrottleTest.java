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

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ThrottleTest {

	@Test
	public void testThrottleTime() throws Exception {
		Throttle throttle = new Throttle("Test");

		// Do an initial throttle
		throttle.throttle();

		DateTime before = new DateTime();

		int throttleDelay = 600;
		throttle.throttle(throttleDelay * 2);

		DateTime after = new DateTime();

		Period period = new Period(before, after, PeriodType.millis());

		Assert.assertTrue(period.getValue(0) > throttleDelay);
	}

	@Test
	public void testThrottling() throws Exception {
		Throttle throttle = new Throttle("Test");

		List<Integer> delays = new ArrayList<>();
		DateTime lastDate = null;

		int throttleTime = 500;

		for (int i = 0; i < 10; i++) {
			throttle.throttle(throttleTime);

			DateTime after = new DateTime();

			if (lastDate != null) {
				delays.add(new Period(lastDate, after, PeriodType.millis()).getValue(0));
			}

			lastDate = after;
		}

		float totalDelay = 0;
		for (int delay : delays) {
			totalDelay += delay;
		}

		float averageDelay = totalDelay / delays.size();

		Assert.assertTrue(Math.abs((averageDelay - throttleTime) / throttleTime) < 0.5);
	}

	@Test
	public void testResolverThrottling() throws Exception {

		List<Integer> delays = new ArrayList<>();
		DateTime lastDate = null;

		int throttleTime = 500;

		for (int i = 0; i < 10; i++) {
			TestThrottleResolver testThrottleResolver = new TestThrottleResolver("http://this.is.a.test.com/" + i * 100, "aDummyDoi", throttleTime);

			testThrottleResolver.resolveWithThrottle();

			DateTime after = new DateTime();

			if (lastDate != null) {
				delays.add(new Period(lastDate, after, PeriodType.millis()).getValue(0));
			}

			lastDate = after;
		}

		float totalDelay = 0;
		for (int delay : delays) {
			totalDelay += delay;
		}

		float averageDelay = totalDelay / delays.size();

		Assert.assertTrue(Math.abs((averageDelay - throttleTime) / throttleTime) < 0.5);
	}

}
