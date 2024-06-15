# Foundations of Kuberentes - Java Edition

This repo contains the source material for my _Foundations of Kubernetes_ talk at CIJUG on June 20th, 2024.

https://www.meetup.com/central-iowa-java-users-group/events/301201025/

Specifically, this repo contains the two demonstration projects:

- [simplenetes](./simplenetes/README.md)
- [dice-roll-operator](./dice-roll-operator/README.md)

## Prerequisites

In order to run these projects, you will need the following:

- [JDK 21](https://openjdk.org/projects/jdk/21/)
- [Docker](https://www.docker.com/)
- [minikube](https://minikube.sigs.k8s.io/docs/)

**Note:** other cluster tools will likely work (e.g. [kind](https://kind.sigs.k8s.io/) or even a managed cloud cluster) but the directions in this repo assume you are using minikube.

### Codespaces

If you don't want to install any of the above tools locally, the default [GitHub Codespaces](https://github.com/features/codespaces) image contains all the necessary tools.

**Note:** the majority of the code was developed using GitHub Codespaces :octocat:

## Running

The README files for the demonstration projects contain specific instructions on how to run the example.