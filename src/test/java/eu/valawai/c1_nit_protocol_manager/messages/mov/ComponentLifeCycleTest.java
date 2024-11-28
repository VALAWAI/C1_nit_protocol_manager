/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link ComponentLifeCycle}.
 *
 * @see ComponentLifeCycle
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@WithTestResource(value = MOVTestResource.class)
public class ComponentLifeCycleTest {

	/**
	 * The current version of the component.
	 */
	@ConfigProperty(name = "quarkus.application.version")
	protected String version;

	/**
	 * The service to query for the component info.
	 */
	@Inject
	protected MOVComponentQueryService queryService;

	/**
	 * The status of the component.
	 */
	@Inject
	protected ComponentStatus status;

	/**
	 * Check that the component is registered.
	 */
	@Test
	public void shouldComponentBeRegistered() {

		for (var i = 0; i < 60 && !this.status.isRegistered(); i++) {

			try {

				Thread.sleep(500);

			} catch (final InterruptedException ignored) {
			}

		}
		final var componentId = this.status.getRegisteredId();
		assertNotNull(componentId);
		final var component = this.queryService.queryComponentInformation();
		assertNotNull(component);
		assertEquals(componentId, component.getString("id"));
		assertEquals(this.version, component.getString("version"));

	}

}
