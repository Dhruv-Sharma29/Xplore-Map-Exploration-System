#!/bin/sh
set -eu
cd "$(dirname "$0")"
exec mvn javafx:run
