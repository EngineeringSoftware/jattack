#!/bin/bash

readonly _DIR="$( cd -P "$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )" )" && pwd )"

# Accept inputs
class="${1:-T}"; shift # default T
n_outputs="${1:-3}"; shift # default 3

# Other configurations we may want to touch
src="${class}.java"
readonly n_invocations=100000
readonly modes=( level4 level1 )
readonly seed=42

# Constants
readonly JATTACK_JAR="${_DIR}/jattack-all.jar"
readonly DOT_DIR="${_DIR}/.jattack"
mkdir -p "${DOT_DIR}"
readonly DL_DIR="${DOT_DIR}/downloads"
mkdir -p "${DL_DIR}"
readonly template_dir="${DOT_DIR}/${class}"
readonly build_dir="${template_dir}/build"
readonly gen_dir="${template_dir}/gen"
readonly output_dir="${template_dir}/output"

mkdir -p "${build_dir}"
mkdir -p "${gen_dir}"
mkdir -p "${output_dir}"

readonly STOP_AT_LEVEL="-XX:TieredStopAtLevel=" # followed by level number
readonly EXTRA_JAVA_FLAGS=""
readonly n_exec_itrs=""


# Download jdk.
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

# Build JAttack.
if [[ ! -f ${JATTACK_JAR} ]]; then
        echo "Build JAttack..."
        readonly SRC_DIR="${_DIR}/tool/api"
        readonly VERSION="$( grep '^version ' "${SRC_DIR}"/build.gradle | cut -d' ' -f2 | tr -d "'" )"
        pushd "${SRC_DIR}" >/dev/null
        ./gradlew -q clean shadowJar
        popd >/dev/null
        cp "${SRC_DIR}/build/libs/sketchy-${VERSION}-all.jar" "${JATTACK_JAR}"
fi

# Compile the template
if [[ ! -f ${src} ]]; then
        echo "ERROR: file not found: ${src}" >&2
        exit 1
fi
javac -cp "${JATTACK_JAR}" "${src}" -d "${build_dir}"
if [[ $? -ne 0 ]]; then
        echo "ERROR: compiling ${src}" >&2
        exit 1
fi

# Generate programs from the template using JAttack
echo "Generate from ${class}..."
rm -fr "${gen_dir}" && mkdir -p "${gen_dir}"
java -javaagent:"${JATTACK_JAR}" -cp "${build_dir}" sketchy.driver.Driver \
     --seed=${seed} \
     --nOutputs=${n_outputs} \
     --outputDir="${gen_dir}" \
     --clzName=${class} \
     --srcPath="${src}" \
     --nInvocations=${n_invocations}
if [[ $? -ne 0 ]]; then
        echo "ERROR: generating from ${src}" >&2
        exit 1
fi

# Print a message
num_gen=$( find "${gen_dir}" -name "*.java" | wc -l )
echo "${num_gen} programs are generated in ${gen_dir}"

# Do differential testing for every generated program
CP="${build_dir}:${JATTACK_JAR}"
fail_clzes=()
for file in $( find ${gen_dir} -name "*.java" | sort -V ); do
        gen_clz="$( basename "${file}" | cut -d '.' -f 1 )"
        output_dir_per_gen="${output_dir}/${gen_clz}"
        rm -fr "${output_dir_per_gen}" && mkdir -p "${output_dir_per_gen}"

        # Compile
        javac -cp "${JATTACK_JAR}" "${file}" -d "${build_dir}"
        if [[ $? -ne 0 ]]; then
                echo "ERROR: compiling ${file}" >&2
                exit 1
        fi

        # Run at all modes
        echo "Executing ${gen_clz}..."
        for mode in "${modes[@]}"; do
                echo "  At ${mode}..."

                output_file="${output_dir_per_gen}/${gen_clz}-${mode}.txt"
                case "${mode}" in
                'level'[01234])
                        level="${mode: -1}"
                        java -cp "${CP}" \
                             ${EXTRA_JAVA_FLAGS} \
                             ${STOP_AT_LEVEL}${level} \
                             ${gen_clz} ${n_exec_itrs} \
                             >"${output_file}" 2>&1
                        if [[ $? -ne 0 ]]; then
                                ## Runtime exception or Error.
                                echo "ERROR: running ${gen_clz} at ${mode}. See ${output_file}" >&2
                        fi
                        ;;
                *)
                        echo "ERROR: unsupported mode: ${mode}" >&2
                        exit 1
                        ;;
                esac
                # cat ${output_file}
        done

        # Compare every two modes
        for (( i=0 ; i < ${#modes[@]} ; i+=1 )); do
                for (( j=$(( ${i}+1 )) ; j < ${#modes[@]} ; j+=1 )); do
                        modeA=${modes[i]}
                        modeB=${modes[j]}

                        diff_file="${output_dir_per_gen}/${gen_clz}-${modeA}-${modeB}-diffs.txt"
                        diff "${output_dir_per_gen}/${gen_clz}-${modeA}.txt" \
                             "${output_dir_per_gen}/${gen_clz}-${modeB}.txt" \
                             >"${diff_file}"
                        if [[ ! -s ${diff_file} ]]; then
                                # remove empty diff file
                                rm -f "${diff_file}"
                        else
                                echo "${gen_clz}: ${modeA} differs with ${modeB}!" >&2
                                fail_clzes+=( "${gen_clz}" )
                        fi
                done
        done
done

# Print results
if [[ ${#fail_clzes[@]} -eq 0 ]]; then
        echo "All passed!"
else
        echo "${fail_clzes[*]} failed! Potential JIT bugs!" >&2
fi
