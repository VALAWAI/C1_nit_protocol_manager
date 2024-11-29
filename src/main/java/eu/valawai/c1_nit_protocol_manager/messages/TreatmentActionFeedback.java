/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

/**
 * The possible feedback over a {@link TreatmentAction}.
 *
 * @see TreatmentPayload
 * @see TreatmentAction
 * @see TreatmentActionFeedbackPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum TreatmentActionFeedback {

	/**
	 * When the action is allowed to do.
	 */
	ALLOW,

	/**
	 * When the action is not allowed to do.
	 */
	DENY,

	/**
	 * When is unknown if the action can be done/ or not because is missing some
	 * information.
	 */
	UNKNOWN;

}
