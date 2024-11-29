/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c1_nit_protocol_manager.messages.mov.MOVTestResource;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link TreatmentActionFeedbackService}.
 *
 * @see TreatmentActionFeedbackService
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@WithTestResource(value = MOVTestResource.class)
public class TreatmentActionFeedbackServiceTest {

	/**
	 * The service to test.
	 */
	@Inject
	TreatmentActionFeedbackService service;

	/**
	 * The queue to capture the send messages.
	 */
	@Inject
	TreatmentActionFeedbackQueue queue;

	/**
	 * Clear the queue before any test.
	 */
	@BeforeEach
	public void clearQueue() {

		this.queue.clearPayloads();
	}

	/**
	 * Should send a payload.
	 */
	@Test
	public void shouldSendPayload() {

		final var expected = new TreatmentActionFeedbackPayloadTest().nextModel();
		this.service.send(expected);
		final var received = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
		assertEquals(expected, received);
	}

	/**
	 * Should send a feedback.
	 */
	@Test
	public void shouldSendTreatmentActionFeedback() {

		final var expected = new TreatmentActionFeedbackPayloadTest().nextModel();
		this.service.send(expected.treatment_id, expected.action, expected.feedback);
		final var received = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
		assertEquals(expected, received);
	}

	/**
	 * Should send allow action.
	 */
	@Test
	public void shouldSendAllow() {

		final var expected = new TreatmentActionFeedbackPayloadTest().nextModel();
		this.service.sendAllow(expected.treatment_id, expected.action);
		expected.feedback = TreatmentActionFeedback.ALLOW;
		final var received = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
		assertEquals(expected, received);
	}

	/**
	 * Should send deny action.
	 */
	@Test
	public void shouldSendDeny() {

		final var expected = new TreatmentActionFeedbackPayloadTest().nextModel();
		this.service.sendDeny(expected.treatment_id, expected.action);
		expected.feedback = TreatmentActionFeedback.DENY;
		final var received = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
		assertEquals(expected, received);
	}

	/**
	 * Should send unknown action.
	 */
	@Test
	public void shouldSendUnknown() {

		final var expected = new TreatmentActionFeedbackPayloadTest().nextModel();
		this.service.sendUnknown(expected.treatment_id, expected.action);
		expected.feedback = TreatmentActionFeedback.UNKNOWN;
		final var received = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
		assertEquals(expected, received);
	}

}
