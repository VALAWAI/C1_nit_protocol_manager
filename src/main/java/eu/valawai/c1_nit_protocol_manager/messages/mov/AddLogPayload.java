/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.c1_nit_protocol_manager.messages.Payload;

/**
 * The information of a log message.
 *
 * @author VALAWAI
 */
@JsonRootName("add_log_payload")
public class AddLogPayload extends Payload {

	/**
	 * The level of the log.
	 */
	public LogLevel level;

	/**
	 * The identifier of the component that has generated the log message.
	 */
	public String component_id;

	/**
	 * The message of the log.
	 */
	public String message;

	/**
	 * The payload of the log.
	 */
	public String payload;

}
