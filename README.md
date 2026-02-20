# C1_nit_protocol_manager

The NIT Protocol Manager (C1) is a component responsible for ensuring the integrity
of the treatment process. It functions by receiving treatment data and meticulously verifying t
hat it adheres to the established NIT protocol specifications. This rigorous validation step
safeguards against potential inconsistencies or errors that could compromise the treatment's efficacy.

## Summary

- **Type**: [C1](https://valawai.github.io/docs/components/C1/)
- **Name**: NIT protocol manager
- **Documentation**: [https://valawai.github.io/docs/components/C1/nit_protocol_manager](https://valawai.github.io/docs/components/C1/nit_protocol_manager)
- **Versions**:
  - **Stable version**: [1.2.2 (February 20, 2026)](https://github.com/VALAWAI/C1_nit_protocol_manager/tree/1.2.2)
  - **API**: [1.0.1 (January 21, 2025)](https://raw.githubusercontent.com/VALAWAI/C1_nit_protocol_manager/ASYNCAPI_1.0.1/asyncapi.yaml)
  - **Required MOV API**: [1.2.0 (March 9, 2024)](https://raw.githubusercontent.com/valawai/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
- **Developed By**: [IIIA-CSIC](https://www.iiia.csic.es)
- **License**: [GPL v3](LICENSE)
- **Technology Readiness Level (TLR)**: [3](https://valawai.github.io/docs/components/C1/nit_protocol_manager/tlr)

## Usage

The C1 NIT Protocol Manager identifies if the treatments proposed by a medical professional follow the NIT protocol. This protocol determines which medical actions are allowed or denied based on the patient's NIT (Therapeutic Intensity Level).

- **Check Treatment**: The component monitors the `valawai/c1/nit_protocol_manager/data/treatment` channel for new treatment plans. Each treatment includes patient status criteria (e.g., age range, clinical risk group) and a list of proposed actions.
- **Provide Action Feedback**: For every treatment received, the component validates the actions against the NIT protocol and publishes the results to the `valawai/c1/nit_protocol_manager/data/treatment_action_feedback` channel. Feedback can be `ALLOW`, `DENY`, or `UNKNOWN`.

## Deployment

The **C1 NIT protocol manager** is designed to run as a Docker container, working within
the [Master Of VALAWAI (MOV)](https://valawai.github.io/docs/architecture/implementations/mov) ecosystem.
For a complete guide, including advanced setups, refer to
the [component's full deployment documentation](https://valawai.github.io/docs/components/C1/nit_protocol_manager/deploy).

Here's how to quickly get it running:

1. ### Build the Docker Image

   First, you need to build the Docker image. Go to the project's root directory and run:

   ```bash
   ./buildDockerImages.sh -t latest
   ```

   This creates the `valawai/c1_nit_protocol_manager:latest` Docker image, which is referenced in the `docker-compose.yml` file.

2. ### Start the Component

   You have two main ways to start the component:

   A. **With MOV:**
   To run the C1 NIT protocol manager with the MOV, use:

   ```bash
   COMPOSE_PROFILES=all docker compose up -d
   ```

   Once started, you can access:
   - **MOV:** [http://localhost:8081](http://localhost:8081)
   - **RabbitMQ UI:** [http://localhost:8082](http://localhost:8082) (credentials: `mov:password`)
   - **Mongo DB:** `localhost:27017` (credentials: `mov:password`)

   B. **As a Standalone Component (connecting to an existing MOV/RabbitMQ):**
   If you already have MOV running or want to connect to a remote RabbitMQ, you'll need a [`.env` file](https://docs.docker.com/compose/environment-variables/env-file/) with connection details. Create a `.env` file in the same directory as your `docker-compose.yml` like this:

   ```properties
   MOV_MQ_HOST=host.docker.internal
   MOV_MQ_USERNAME=mov
   MOV_MQ_PASSWORD=password
   C1_NIT_PROTOCOL_MANAGER_PORT=9080
   ```

   Find full details on these and other variables in the [component's dedicated deployment documentation](https://valawai.github.io/docs/components/C1/nit_protocol_manager/deploy).
   Once your `.env` file is configured, start only the NIT protocol manager (without MOV) using:

   ```bash
   COMPOSE_PROFILES=component docker compose up -d
   ```

3. ### Stop All Containers

    To stop all containers launched, run:

    ```bash
    COMPOSE_PROFILES=all docker compose down
    ```

    This command stops the MOV, RabbitMQ, and Mongo containers.


## Development environment

To ensure a consistent and isolated development experience, this component is configured
to use Docker. This approach creates a self-contained environment with all the necessary
software and tools for building and testing, minimizing conflicts with your local system
and ensuring reproducible results.

You can launch the development environment by running this script:

```bash
./startDevelopmentEnvironment.sh
```

Once the environment starts, you'll find yourself in a bash shell, ready to interact with
the Quarkus development environment. You'll also have access to the following integrated tools:

- **Master of VALAWAI**: The central component managing topology connections between services.
  Its web interface is accessible at [http://localhost:8081](http://localhost:8081).
- **RabbitMQ** The message broker for inter-component communication. The management web interface
  is at [http://localhost:8082](http://localhost:8082), with credentials `mov**:**password`.
- **MongoDB**: The database used by the MOV, named `movDB`, with user credentials `mov:password`.
- **Mongo express**: A web interface for interacting with MongoDB, available at
  [http://localhost:8084](http://localhost:8084), also with credentials `mov**:**password`.

Within this console, you can use the official [`quarkus` client](https://quarkus.io/guides/cli-tooling#using-the-cli)
or any of these convenient commands:

- `startServer`: To initiate the development server.
- `mvn clean`: To clean the project (compiled and generated code).
- `mvn test`: To run all project tests.
- `mvn -DuseDevMOV=true test`: To execute tests using the already started Master of VALAWAI instance,
  rather than an independent container.

To exit the development environment, simply type `exit` in the bash shell or run the following script:

```bash
./stopDevelopmentEnvironment.sh
```

In either case, the development environment will gracefully shut down, including all activated services
like MOV, RabbitMQ, MongoDB, and Mongo Express.

## Helpful Links

Here's a collection of useful links related to this component and the VALAWAI ecosystem:

- **C1 NIT Protocol Manager Documentation**: [https://valawai.github.io/docs/components/C1/nit_protocol_manager](https://valawai.github.io/docs/components/C1/nit_protocol_manager)
- **Master Of VALAWAI (MOV)**: [https://valawai.github.io/docs/architecture/implementations/mov/](https://valawai.github.io/docs/architecture/implementations/mov/)
- **VALAWAI Main Documentation**: [https://valawai.github.io/docs/](https://valawai.github.io/docs/)
- **VALAWAI on GitHub**: [https://github.com/VALAWAI](https://github.com/VALAWAI)
- **VALAWAI Official Website**: [https://valawai.eu/](https://valawai.eu/)
- **VALAWAI on X (formerly Twitter)**: [https://x.com/ValawaiEU](https://x.com/ValawaiEU)
