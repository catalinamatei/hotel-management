#!/bin/bash
CURRENT_BRANCH=$(git branch --show-current)

DOCKER_COMPOSE_FILE="docker-compose.yaml"

if [[ "$#" -eq 0 ]]
then
    docker compose -f $DOCKER_COMPOSE_FILE down
else
    docker compose -f $DOCKER_COMPOSE_FILE stop "$@"
fi
