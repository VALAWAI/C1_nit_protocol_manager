/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import java.text.MessageFormat;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The service to send log messages to the Master Of VALAWAI.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class LogService {

	/**
	 * The component to send the log messages.
	 */
	@Channel("send_log")
	@Inject
	Emitter<AddLogPayload> service;

	/**
	 * The status of the component.
	 */
	@Inject
	protected ComponentStatus status;

	/**
	 * Send a log into the Master Of VALAWAI.
	 *
	 * @param payload with the log to report.
	 */
	public void send(AddLogPayload payload) {

		payload.component_id = this.status.getRegisteredId();
		Uni.createFrom().completionStage(this.service.send(payload)).subscribe().with(
				any -> Log.debugv("Sent log {0}.", payload),
				error -> Log.errorv(error, "Cannot send log {0}.", payload));
	}

	/**
	 * Send a info log message.
	 *
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void info(String message, Object... params) {

		this.send(LogLevel.INFO, null, message, params);
	}

	/**
	 * Send a info log message.
	 *
	 * @param payload for the message.
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void infoWithPayload(Object payload, String message, Object... params) {

		this.send(LogLevel.INFO, payload, message, params);
	}

	/**
	 * Send a error log message.
	 *
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void error(String message, Object... params) {

		this.send(LogLevel.ERROR, null, message, params);
	}

	/**
	 * Send a error log message.
	 *
	 * @param payload for the message.
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void errorWithPayload(Object payload, String message, Object... params) {

		this.send(LogLevel.ERROR, payload, message, params);
	}

	/**
	 * Send a debug log message.
	 *
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void debug(String message, Object... params) {

		this.send(LogLevel.DEBUG, null, message, params);
	}

	/**
	 * Send a debug log message.
	 *
	 * @param payload for the message.
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void debugWithPayload(Object payload, String message, Object... params) {

		this.send(LogLevel.DEBUG, payload, message, params);
	}

	/**
	 * Send a warning log message.
	 *
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void warning(String message, Object... params) {

		this.send(LogLevel.WARN, null, message, params);
	}

	/**
	 * Send a warning log message.
	 *
	 * @param payload for the message.
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void warningWithPayload(Object payload, String message, Object... params) {

		this.send(LogLevel.WARN, payload, message, params);
	}

	/**
	 * Send a log message.
	 *
	 * @param level   of the log.
	 * @param payload for the message.
	 * @param message of the log message.
	 * @param params  parameters to replace on the message.
	 */
	public void send(LogLevel level, Object payload, String message, Object... params) {

		final var log = new AddLogPayload();
		log.level = level;
		log.message = MessageFormat.format(message, params);
		if (payload != null) {

			log.payload = Json.encodePrettily(payload);
		}
		this.send(log);

	}

}
