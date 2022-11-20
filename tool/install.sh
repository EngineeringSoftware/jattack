#!/bin/bash

readonly _DIR="$( cd -P "$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )" )" && pwd )"

set -e

### Build JAttack jar. Java >=11
echo "Build JAttack jar..."
pushd "${_DIR}"/api >/dev/null
./gradlew -q clean shadowJar
popd >/dev/null
cp "${_DIR}"/api/build/libs/jattack-*-all.jar "${_DIR}"/jattack-all.jar

### Install python packages. Python 3.8
echo "Install python packages..."
pip install -qr "${_DIR}"/requirements.txt

### Create executable script.
exec="${_DIR}"/jattack
echo "#!/bin/bash" >"$exec"
echo 'readonly _DIR="$( cd -P "$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )" )" && pwd )"' >>"$exec"
echo 'python3 ${_DIR}/jattack.py "$@"' >>"$exec"
chmod +x "$exec"
