/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The queue to capture the sent {@link TreatmentActionFeedbackPayload}.
 *
 * @see TreatmentActionFeedbackPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class TreatmentActionFeedbackQueue {

	/**
	 * The queue of received e-mails.
	 */
	protected List<TreatmentActionFeedbackPayload> queue = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Wait until the queue receive an treatment.
	 *
	 * @param duration maximum time to wait to receive the e-mail.
	 *
	 * @return the oldest e-mial received or {@code null} if any mail is received.
	 */
	public TreatmentActionFeedbackPayload waitUntilNextTreatment(Duration duration) {

		synchronized (this.queue) {

			if (this.queue.isEmpty()) {

				try {
					this.queue.wait(duration.toMillis());
				} catch (final InterruptedException ignored) {
				}
				if (this.queue.isEmpty()) {
					return null;
				}
			}

			return this.queue.remove(0);
		}
	}

	/**
	 * Remove any e-mial message from the queue.
	 */
	public void clearPayloads() {

		this.queue.clear();
	}

	/**
	 * Called when a new e-mail is sensed by the component.
	 *
	 * @param msg with the e-mail that has been sensed.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("published_treatment_action_feedback")
	public CompletionStage<Void> publishedTreatment(Message<JsonObject> msg) {

		try {

			synchronized (this.queue) {

				final var payload = msg.getPayload().mapTo(TreatmentActionFeedbackPayload.class);
				this.queue.add(payload);
				this.queue.notifyAll();
			}
			return msg.ack();

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot get the sensed treatment.");
			return msg.nack(error);

		}
	}

	/**
	 * Check if the queue is empty.
	 *
	 * @return {@code true} if the queue is empty.
	 */
	public boolean isEmpty() {

		return this.queue.isEmpty();
	}

}
