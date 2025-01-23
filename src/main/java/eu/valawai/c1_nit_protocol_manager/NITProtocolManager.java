/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import eu.valawai.c1_nit_protocol_manager.messages.TreatmentActionFeedbackService;
import eu.valawai.c1_nit_protocol_manager.messages.TreatmentPayload;
import eu.valawai.c1_nit_protocol_manager.messages.mov.LogService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * The manager that check that a treatment follow the NIT protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class NITProtocolManager {

	/**
	 * The rule engine.
	 */
	KieHelper helper;

	/**
	 * The component to send log messages to MOV.
	 */
	@Inject
	LogService log;

	/**
	 * The type of norms to be used.
	 */
	@ConfigProperty(name = "c1_nit_protocol_manager.norms.type", defaultValue = "DRL")
	String normsType;

	/**
	 * The path to the file with the norms to be used.
	 */
	@ConfigProperty(name = "c1_nit_protocol_manager.norms.file", defaultValue = "eu/valawai/c1_nit_protocol_manager/nit-protocol.drl")
	String normsFile;

	/**
	 * The service to notify for the treatment feedback.
	 */
	@Inject
	TreatmentActionFeedbackService service;

	/**
	 * The component to validate a {@code Payload}.
	 */
	@Inject
	Validator validator;

	/**
	 * Called when the component is started and it start the norm engine.
	 *
	 * @param event that contains the start status.
	 */
	public void handle(@Observes StartupEvent event) {

		try {

			this.helper = new KieHelper();
			final var type = ResourceType.determineResourceType(this.normsType);
			var content = "";
			final var path = Path.of(this.normsFile);
			final var file = path.toFile();
			if (file.exists() && file.canRead()) {

				content = Files.readString(path);

			} else {

				final var loader = this.getClass().getClassLoader();
				final var stream = loader.getResourceAsStream(this.normsFile);
				content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			}

			this.helper.addContent(content, type);
			Log.debugv("Started the norm engine with the {0} norms in {1}.", this.normsType, this.normsFile);

		} catch (final Throwable error) {

			this.log.error("Cannot start norm engine, because {0}.", error.getMessage());
			Log.errorv(error, "Cannot start the norm engine.");
			this.helper = null;
		}

	}

	/**
	 * Check if the norm engine has been started.
	 *
	 * @return {@code true} if the norm engine is up.
	 */
	public boolean hasNormEngine() {

		return this.helper != null;
	}

	/**
	 * Check if the treatment follow the NIT protocol.
	 *
	 * @see TreatmentPayload
	 *
	 * @param msg with treatment to process.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("received_treatment")
	public CompletionStage<Void> checkTreatment(Message<JsonObject> msg) {

		final var payload = msg.getPayload();

		try {

			final var treatment = payload.mapTo(TreatmentPayload.class);
			final var violations = this.validator.validate(treatment);
			if (!violations.isEmpty()) {

				throw new ConstraintViolationException(violations);

			} else {

				this.log.debugWithPayload(payload, "Received a treatment to check if it follows the NIT protocol.");
				final var session = this.helper.build().newKieSession();
				try {

					session.setGlobal("service", this.service);
					session.insert(treatment);
					session.fireAllRules();

				} finally {

					session.dispose();
				}
				return msg.ack();
			}

		} catch (final Throwable error) {

			this.log.errorWithPayload(payload, "Cannot check if the treatment follow the NIT protocol, because {0}.",
					error.getMessage());
			Log.errorv(error, "Cannot check if the treatment follow the NIT protocol.");
			return msg.nack(error);
		}

	}

}
