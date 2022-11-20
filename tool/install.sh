#!/bin/bash

set -e

### Build JAttack jar. Java >=11
echo "Build JAttack jar..."
pushd api >/dev/null
./gradlew clean shadowJar
popd >/dev/null
cp api/build/libs/jattack-*-all.jar jattack-all.jar

### Install python packages. Python 3.8
echo "Install python packages..."
pip install -r requirements.txt

### Create executable script.
echo "#!/bin/bash" >jattack
echo 'python jattack.py "$@"' >jattack
chmod +x jattack
