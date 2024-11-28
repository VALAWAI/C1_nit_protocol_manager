/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import eu.valawai.c1_nit_protocol_manager.ValueGenerator;
import eu.valawai.c1_nit_protocol_manager.messages.PayloadTestCase;
import io.vertx.core.json.Json;

/**
 * Test the {@link AddLogPayload}.
 *
 * @see AddLogPayload
 *
 * @author VALAWAI
 */
public class AddLogPayloadTest extends PayloadTestCase<AddLogPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AddLogPayload createEmptyModel() {

		return new AddLogPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(AddLogPayload payload) {

		final var values = LogLevel.values();
		payload.level = values[ValueGenerator.nextInt(0, values.length)];
		payload.message = "Log message " + ValueGenerator.nextDouble();
		payload.payload = Json.encodePrettily(new QueryComponentsPayload());
	}

}
