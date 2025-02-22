/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import eu.valawai.c1_nit_protocol_manager.messages.mov.LogService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The service to send {@link TreatmentActionFeedbackPayload}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class TreatmentActionFeedbackService {

	/**
	 * The component to send log messages to MOV.
	 */
	@Inject
	LogService log;

	/**
	 * The service to send the treatment action feedbacks.
	 */
	@Inject
	@Channel("send_treatment_action_feedback")
	protected Emitter<TreatmentActionFeedbackPayload> service;

	/**
	 * Send the specified payload.
	 *
	 * @param payload to send.
	 */
	public void send(TreatmentActionFeedbackPayload payload) {

		Uni.createFrom().completionStage(this.service.send(payload)).subscribe().with(
				any -> this.log.debugWithPayload(payload, "Sent if a treatment action follows the NIT protocol."),
				error -> this.log.errorWithPayload(payload, "Cannot send the action feedback."));

	}

	/**
	 * Send the specified treatment action feedback.
	 *
	 * @param treatmentId identifier of the treatment.
	 * @param action      to provide feedback.
	 * @param feedback    of the action.
	 */
	public void send(String treatmentId, TreatmentAction action, TreatmentActionFeedback feedback) {

		final var payload = new TreatmentActionFeedbackPayload();
		payload.treatment_id = treatmentId;
		payload.action = action;
		payload.feedback = feedback;
		this.send(payload);
	}

	/**
	 * Send that the treatment action is allowed.
	 *
	 * @param treatmentId identifier of the treatment.
	 * @param action      the allowed treatment action.
	 */
	public void sendAllow(String treatmentId, TreatmentAction action) {

		this.send(treatmentId, action, TreatmentActionFeedback.ALLOW);
	}

	/**
	 * Send that the treatment action is denied.
	 *
	 * @param treatmentId identifier of the treatment.
	 * @param action      the denied treatment action.
	 */
	public void sendDeny(String treatmentId, TreatmentAction action) {

		this.send(treatmentId, action, TreatmentActionFeedback.DENY);
	}

	/**
	 * Send that the treatment action is not possible to known if it can be done or
	 * not, because it required extra information.
	 *
	 * @param treatmentId identifier of the treatment.
	 * @param action      the treatment action that is unknown.
	 */
	public void sendUnknown(String treatmentId, TreatmentAction action) {

		this.send(treatmentId, action, TreatmentActionFeedback.UNKNOWN);
	}

}
