#!/usr/bin/env bash

./gradlew assemble
docker build -t delius-case-notes-api .

# To run within Docker:
# docker run -d delius-case-notes-api -e {...}
