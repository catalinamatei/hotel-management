#!/bin/bash

CURRENT_BRANCH=$(git branch --show-current)

DOCKER_COMPOSE_FILE="docker-compose.yaml"

docker compose -f $DOCKER_COMPOSE_FILE up --build -d "$@"
