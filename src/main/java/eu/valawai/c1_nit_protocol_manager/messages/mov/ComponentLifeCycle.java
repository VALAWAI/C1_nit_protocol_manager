/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages.mov;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static jakarta.interceptor.Interceptor.Priority.PLATFORM_BEFORE;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * The class used to manage the component life cycle. Thus, this register the
 * component when started and unregister when shutdown.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class ComponentLifeCycle {

	/**
	 * The queue to send the registration message.
	 */
	@Inject
	@Channel("send_register_component")
	protected Emitter<RegisterComponentPayload> register;

	/**
	 * The queue to send the registration message.
	 */
	@Inject
	@Channel("send_unregister_component")
	protected Emitter<UnregisterComponentPayload> unregister;

	/**
	 * The current version of the component.
	 */
	@ConfigProperty(name = "quarkus.application.version")
	protected String version;

	/**
	 * The current version of the component.
	 */
	@ConfigProperty(name = "c1_nit_protocol_manager.send.timeout", defaultValue = "60")
	protected long sendTimeout;

	/**
	 * The status of the component.
	 */
	@Inject
	protected ComponentStatus status;

	/**
	 * Called when the component is started and it must to register it on the
	 * VALAWAI infrastructure.
	 *
	 * @param event that contains the start status.
	 */
	public void onStartUp(@Observes @Priority(APPLICATION + 1) final StartupEvent event) {

		try {

			final var payload = new RegisterComponentPayload();
			payload.version = this.version;
			Uni.createFrom().completionStage(this.register.send(payload)).await()
					.atMost(Duration.ofSeconds(this.sendTimeout));
			Log.debugv("Sent register {0}.", payload);

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot send the component registration message.");
			Quarkus.asyncExit(1);
		}

	}

	/**
	 * Called when the component is finished and it has to unregister it from the
	 * VALAWAI infrastructure.
	 *
	 * @param event that contains the start status.
	 */
	public void onShutdown(@Observes @Priority(PLATFORM_BEFORE + 1) ShutdownEvent event) {

		if (this.status.isRegistered()) {

			try {

				final var payload = new UnregisterComponentPayload();
				payload.component_id = this.status.getRegisteredId();
				Uni.createFrom().completionStage(this.unregister.send(payload)).await()
						.atMost(Duration.ofSeconds(this.sendTimeout));
				Log.debugv("Sent unregister {0}.", payload);

			} catch (final Throwable error) {

				Log.errorv(error, "Cannot send the component unregister message.");
			}

		}

	}

	/**
	 * Called when the component is registered.
	 *
	 * @param payload with the component information.
	 */
	@Incoming("registered")
	public void handleControlRegistered(JsonObject payload) {

		final var componentId = payload.getString("id");
		this.status.registered(componentId);
		Log.infov("Registered component {0}.", payload);

	}

}
