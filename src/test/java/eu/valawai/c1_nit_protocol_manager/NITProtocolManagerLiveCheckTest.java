/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.valawai.c1_nit_protocol_manager.messages.mov.MOVTestResource;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link NITProtocolManagerLiveCheck}.
 *
 * @see NITProtocolManagerLiveCheck
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@WithTestResource(value = MOVTestResource.class)
public class NITProtocolManagerLiveCheckTest {

	/**
	 * The component to manage JSON objects.
	 */
	@Inject
	protected ObjectMapper mapper;

	/**
	 * The NIT protocol manager to check.
	 */
	@Inject
	NITProtocolManager manager;

	/**
	 * Check that the health is down.
	 */
	@Test
	public void shouldHealthBeDown() {

		final var check = new NITProtocolManagerLiveCheck();
		check.manager = Mockito.mock(NITProtocolManager.class);
		Mockito.when(check.manager.hasNormEngine()).thenReturn(false);
		final var result = check.call();
		assertEquals(HealthCheckResponse.Status.DOWN, result.getStatus());

	}

	/**
	 * Check that the health is up.
	 */
	@Test
	public void shouldHealthBeUp() {

		final var check = new NITProtocolManagerLiveCheck();
		check.manager = Mockito.mock(NITProtocolManager.class);
		Mockito.when(check.manager.hasNormEngine()).thenReturn(true);
		final var result = check.call();
		assertEquals(HealthCheckResponse.Status.UP, result.getStatus());

	}

	/**
	 * Check can get health status from the URL.
	 *
	 * @throws JsonProcessingException If can not obtain the status json result.
	 * @throws JsonMappingException    If can not obtain the status json result.
	 */
	@Test()
	public void shouldCheckHealth() throws JsonMappingException, JsonProcessingException {

		boolean found = false;
		final var content = given().when().get("/q/health").then().statusCode(200).extract().asString();
		final var body = this.mapper.readTree(content);
		final var checks = body.get("checks");
		final var max = checks.size();
		for (var i = 0; i < max; i++) {

			final var healthCheck = checks.get(i);
			if (NITProtocolManagerLiveCheck.NAME.equals(healthCheck.get("name").asText())) {

				final var isUp = HealthCheckResponse.Status
						.valueOf(healthCheck.get("status").asText()) == HealthCheckResponse.Status.UP;
				assertEquals(this.manager.hasNormEngine(), isUp);
				found = true;
				break;
			}

		}

		assertTrue(found, "Not found component live check");
	}

}
