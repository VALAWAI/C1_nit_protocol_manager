/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c1_nit_protocol_manager.messages.NITLevel;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentAction;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentActionFeedback;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentActionFeedbackQueue;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentPayload;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentPayloadTest;
import eu.valawai.c1_nit_protocol_manager.messages.mov.LogAsserts;
import eu.valawai.c1_nit_protocol_manager.messages.mov.LogLevel;
import eu.valawai.c1_nit_protocol_manager.messages.mov.MOVTestResource;
import io.quarkus.logging.Log;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test {@link NITProtocolManager}.
 *
 * @see NITProtocolManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@WithTestResource(value = MOVTestResource.class)
public class NITProtocolManagerTest {

	/**
	 * The service to send the treatment.
	 */
	@Inject
	@Channel("send_treatment")
	protected Emitter<TreatmentPayload> service;

	/**
	 * The service to assert that the log.
	 */
	@Inject
	LogAsserts logAsserts;

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
	 * Send a treatment.
	 *
	 * @param treatment to send.
	 */
	public void assertSendTreatment(TreatmentPayload treatment) {

		this.service.send(treatment).handle((success, error) -> {

			if (error == null) {

				Log.infov("Sent {0}.", treatment);

			} else {

				Log.errorv(error, "Cannot send {0}.", treatment);
			}
			return null;
		});

	}

	/**
	 * Check that not process bad treatment.
	 */
	@Test
	public void shouldNotProcessInValidTreatement() {

		final var treatement = new TreatmentPayload();
		this.assertSendTreatment(treatement);
		this.logAsserts
				.waitUntiLogMatch(LogAsserts.withLogLevel(LogLevel.ERROR).and(LogAsserts.withLogPayload(treatement)));

	}

	/**
	 * Check that the treatment action on NIT level 1 are validated.
	 */
	@Test
	public void shouldValidateNITLevel1() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.ONE;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			assertEquals(TreatmentActionFeedback.ALLOW, sent.feedback);

		} while (!actions.isEmpty());

	}

	/**
	 * Check that the treatment action on NIT level 2A are validated.
	 */
	@Test
	public void shouldValidateNITLevel2A() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.TWO_A;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			var expectedFeedback = TreatmentActionFeedback.ALLOW;
			if (sent.action == TreatmentAction.CPR) {

				expectedFeedback = TreatmentActionFeedback.DENY;
			}
			assertEquals(expectedFeedback, sent.feedback);

		} while (!actions.isEmpty());

	}

	/**
	 * Check that the treatment action on NIT level 2B are validated.
	 */
	@Test
	public void shouldValidateNITLevel2B() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.TWO_B;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			var expectedFeedback = TreatmentActionFeedback.ALLOW;
			if (sent.action == TreatmentAction.CPR || sent.action == TreatmentAction.ICU) {

				expectedFeedback = TreatmentActionFeedback.DENY;

			} else if (sent.action == TreatmentAction.TRANSPLANT || sent.action == TreatmentAction.PALLIATIVE_SURGERY
					|| sent.action == TreatmentAction.CURE_SURGERY) {

				expectedFeedback = TreatmentActionFeedback.UNKNOWN;
			}
			assertEquals(expectedFeedback, sent.feedback);

		} while (!actions.isEmpty());

	}

	/**
	 * Check that the treatment action on NIT level 3 are validated.
	 */
	@Test
	public void shouldValidateNITLevel3() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.THREE;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			var expectedFeedback = TreatmentActionFeedback.UNKNOWN;
			if (sent.action == TreatmentAction.CPR || sent.action == TreatmentAction.ICU) {

				expectedFeedback = TreatmentActionFeedback.DENY;
			}
			assertEquals(expectedFeedback, sent.feedback);

		} while (!actions.isEmpty());

	}

	/**
	 * Check that the treatment action on NIT level 4 are validated.
	 */
	@Test
	public void shouldValidateNITLevel4() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.FOUR;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			var expectedFeedback = TreatmentActionFeedback.DENY;
			if (sent.action == TreatmentAction.VASOACTIVE_DRUGS || sent.action == TreatmentAction.NIMV) {

				expectedFeedback = TreatmentActionFeedback.UNKNOWN;
			}
			assertEquals(expectedFeedback, sent.feedback);

		} while (!actions.isEmpty());

	}

	/**
	 * Check that the treatment action on NIT level 5 are validated.
	 */
	@Test
	public void shouldValidateNITLevel5() {

		final var treatment = new TreatmentPayloadTest().nextModel();
		treatment.before_status.nit_level = NITLevel.FIVE;
		final var actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		treatment.actions = actions;
		ValueGenerator.shuffle(treatment.actions);
		this.assertSendTreatment(treatment);

		do {

			final var sent = this.queue.waitUntilNextTreatment(Duration.ofSeconds(30));
			assertNotNull(sent, "Not process treatment");
			assertEquals(treatment.id, sent.treatment_id);
			assertTrue(actions.remove(sent.action), "Unexpected action: " + sent.action);
			var expectedFeedback = TreatmentActionFeedback.DENY;
			if (sent.action == TreatmentAction.NIMV) {

				expectedFeedback = TreatmentActionFeedback.UNKNOWN;
			}
			assertEquals(expectedFeedback, sent.feedback);

		} while (!actions.isEmpty());

	}

}
