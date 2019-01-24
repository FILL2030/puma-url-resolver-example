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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Throttle {

	private static final Logger log = LoggerFactory.getLogger(Throttle.class);
	public static int DEFAULT_THROTTLE_TIME_MILLIS = 20000;

	private Long lastThrottleDateMs = null;
	private String name;
	private boolean isThrottling = false;

	public Throttle(String name) {
		this.name = name;
	}

	public static void setDefaultDelay(int delay) {
		DEFAULT_THROTTLE_TIME_MILLIS = delay;
	}

	public synchronized void throttle() {
		this.throttle(DEFAULT_THROTTLE_TIME_MILLIS);
	}

	// Return false if busy
	public boolean throttleOrBusy(Integer throttleTimeMillis) {
		if (!this.isThrottling) {
			this.throttle(throttleTimeMillis);

			return true;
		}
		return false;
	}

	public synchronized void throttle(Integer throttleTimeMillis) {
		this.isThrottling = true;
		int millisecondsDelay = this.getThrottleDelayMillis(throttleTimeMillis);

		if (millisecondsDelay > 0) {
			log.info("Throttling (" + this.name + ") for " + millisecondsDelay + "ms");

			try {
				Thread.sleep(millisecondsDelay);

			} catch (InterruptedException e) {
				// Fail... oh well
				log.warn("Scheduled future failed");
			}

			log.info("Throttle finished (" + this.name + ")");
		}

		// Update last date;
		this.lastThrottleDateMs = System.currentTimeMillis();
		this.isThrottling = false;
	}

	private int getThrottleDelayMillis(Integer throttleTimeMillis) {
		if (this.lastThrottleDateMs != null) {
			if (throttleTimeMillis == null) {
				throttleTimeMillis = DEFAULT_THROTTLE_TIME_MILLIS;
			}

			Long currentTimeMs = System.currentTimeMillis();

			// Add a random element to the throttle
			throttleTimeMillis = (int)(2 * throttleTimeMillis * Math.random());

			long millisecondsDelay = throttleTimeMillis - (currentTimeMs - this.lastThrottleDateMs);
			millisecondsDelay = Math.max(0, millisecondsDelay);

			return (int)millisecondsDelay;
		}

		return 0;
	}

}
