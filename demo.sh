#!/bin/bash

readonly _DIR="$( cd -P "$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )" )" && pwd )"

# Download jdk.
readonly DL_DIR="${_DIR}/.jattack/downloads"
mkdir -p "${DL_DIR}"
readonly JDK_DIR="${DL_DIR}/jdk-11.0.8+10"
if [[ ! -f ${JDK_DIR}/bin/java || ! -f ${JDK_DIR}/bin/javac ]]; then
        echo "Download JDK..."
        rm -f "${DL_DIR}/openjdk11.tar.gz"
        wget -q 'https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.8%2B10/OpenJDK11U-jdk_x64_linux_hotspot_11.0.8_10.tar.gz' -O ${DL_DIR}/openjdk11.tar.gz
        if [[ $? -ne 0 ]]; then
                echo "ERROR: Downloading JDK failed!" >&2
                exit 1
        fi
        rm -fr "${JDK_DIR}" && mkdir -p "${JDK_DIR}"
        tar xf "${DL_DIR}/openjdk11.tar.gz" -C "${JDK_DIR}" --strip-components=1 \
                && rm -f "${DL_DIR}/openjdk11.tar.gz"
fi
export PATH="${JDK_DIR}/bin:${PATH}"
export JAVA_HOME="${JDK_DIR}"

# Run jattack
#./tool/jattack T 3
python ./tool/jattack.py --clz T --n_gen 3 --seed 42
