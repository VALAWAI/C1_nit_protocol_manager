/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The component to check if the NIT protocol manager is up or not.
 *
 * @see NITProtocolManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
@Liveness
public class NITProtocolManagerLiveCheck implements HealthCheck {

	/**
	 * The name of the check.
	 */
	public static final String NAME = "Norm engine";

	/**
	 * The NIt protocol manager.
	 */
	@Inject
	NITProtocolManager manager;

	/**
	 * Check if the norm engine is up or not.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public HealthCheckResponse call() {

		if (this.manager.hasNormEngine()) {

			return HealthCheckResponse.up(NAME);

		} else {

			return HealthCheckResponse.down(NAME);
		}
	}
}
