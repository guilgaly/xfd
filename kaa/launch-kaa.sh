#!/bin/sh

docker-compose up -d && \
docker-compose exec kaa_0 kaa/tail-node.sh
