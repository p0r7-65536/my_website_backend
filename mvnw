#!/bin/sh

set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
DIST_URL=$(sed -n 's/^distributionUrl=//p' "$WRAPPER_PROPS")
DIST_NAME=$(basename "$DIST_URL" .zip)
MVN_HOME_NAME=${DIST_NAME%-bin}
DIST_DIR="$BASE_DIR/.mvn/wrapper/dists/$DIST_NAME"
MVN_BIN="$DIST_DIR/$MVN_HOME_NAME/bin/mvn"

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

if [ ! -x "$MVN_BIN" ]; then
  mkdir -p "$DIST_DIR"
  ZIP_FILE="$DIST_DIR/$DIST_NAME.zip"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DIST_URL" -o "$ZIP_FILE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DIST_URL" -O "$ZIP_FILE"
  else
    echo "curl or wget is required to download Maven." >&2
    exit 1
  fi

  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$ZIP_FILE" -d "$DIST_DIR"
  else
    echo "unzip is required to extract Maven." >&2
    exit 1
  fi
fi

exec "$MVN_BIN" "$@"
