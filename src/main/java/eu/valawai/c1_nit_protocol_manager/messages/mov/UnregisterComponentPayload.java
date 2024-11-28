/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.c1_nit_protocol_manager.messages.Payload;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The information necessary to unregister a component.
 *
 * @author VALAWAI
 */
@RegisterForReflection
@JsonRootName("unregister_component_payload")
public class UnregisterComponentPayload extends Payload {

	/**
	 * The identifier of the component to unregister.
	 */
	public String component_id;

}
