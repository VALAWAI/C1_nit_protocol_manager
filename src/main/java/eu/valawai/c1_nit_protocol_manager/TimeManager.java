/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c1_nit_protocol_manager;

import java.time.Clock;
import java.time.Instant;

/**
 * The component used to manage the time values.
 *
 * @author VALAWAI
 */
public interface TimeManager {

	/**
	 * Return the current time.
	 *
	 * @return the seconds since the epoch of 1970-01-01T00:00:00Z.
	 */
	static long now() {

		return toTime(Instant.now(Clock.systemUTC()));

	}

	/**
	 * Return the time associated to an instant.
	 *
	 * @param instant to convert too time.
	 *
	 * @return the seconds since the epoch of 1970-01-01T00:00:00Z.
	 */
	static long toTime(final Instant instant) {

		return Math.round(instant.toEpochMilli() / 1000.0);

	}

	/**
	 * Return the time associated to an instant.
	 *
	 * @param time in seconds since the epoch of 1970-01-01T00:00:00Z.
	 *
	 * @return the instant associated to the time.
	 */
	static Instant fromTime(final long time) {

		return Instant.ofEpochSecond(time);

	}

}
