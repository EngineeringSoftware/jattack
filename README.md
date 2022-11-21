# JAttack

JAttack is a framework that enables template-based testing for
compilers. Using JAttack, compiler developers can write templates in
the same language as the compiler they are testing (Java), enabling
them to leverage their domain knowledge to set up a code structure
likely to lead to compiler optimizations while leaving holes
representing expressions they want explored. JAttack executes
templates, exploring possible expressions for holes and filling them
in, generating programs to later be run on compilers. JAttack blends
the power of developers insights, who are providing templates, and
random testing to detect critical bugs.

## Table of contents

1. [Demo](#Demo)
2. [Requirements](#Requirements)
3. [Install](#Install)
4. [Use](#Use)
5. [Docs](#Docs)
6. [Hall of Fame](#Hall-of-Fame)
7. [Citation](#Citation)
8. [Contact](#Contact)

## Demo

This demo reproduces a bug of OpenJDK jdk-11.0.8+10 C2 JIT compiler
using template `T.java`.

1. Developers write a template program using JAttack's DSL fully
   embedded in Java, for example, `T.java`.

```java
import jattack.annotation.Entry;
import static jattack.Boom.*;

public class T {

    static int s1;
    static int s2;

    @Entry
    public static int m() {
        int[] arr = { s1++, s2, 1, 2, intVal().eval() };
        for (int i = 0; i < arr.length; ++i) {
            if (intIdOrIntArrAccessExp().eval() <= s2
                    || relation(intId("s2"), intIdOrIntArrAccessExp(), LE).eval()) {
                arr[i] &= arithmetic(intId(), intArrAccessExp(), ADD, MUL).eval();
            }
        }
        return s1 + s2;
    }
}
```

2. JAttack executes the given template to generate concrete Java
   programs. For example, One of the generated programs from the
   template `T.java` can be `TGen1.java`.

```java
import jattack.annotation.Entry;
import static jattack.Boom.*;
import org.csutil.checksum.WrappedChecksum;

public class TGen1 {

    static int s1;

    static int s2;

    public static int m() {
        int[] arr = { s1++, s2, 1, 2, -1170105035 };
        for (int i = 0; i < arr.length; ++i) {
            if (i <= s2 || (s2 <= arr[2])) {
                arr[i] &= (s2 + arr[0]);
            }
        }
        return s1 + s2;
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                cs.update(m());
            } catch (Throwable e) {
                if (e instanceof jattack.exception.InvokedFromNotDriverException) {
                    throw e;
                }
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(TGen1.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
```

3. JAttack runs every generated program across Java JIT compilers
   under test. For example, running the generated program `TGen1.java`
   crashes C2 in openjdk-11.0.8.

```
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00007f55deedd845, pid=432431, tid=432442
#
# JRE version: OpenJDK Runtime Environment AdoptOpenJDK (11.0.8+10) (build 11.0.8+10)
# Java VM: OpenJDK 64-Bit Server VM AdoptOpenJDK (11.0.8+10, mixed mode, tiered, compressed oops, g1 gc, linux-amd64)
# Problematic frame:
# V  [libjvm.so+0xd60845]  ok_to_convert(Node*, Node*)+0x15
#
# Core dump will be written. Default location: Core dumps may be processed with "/usr/share/apport/apport -p%p -s%s -c%c -d%d -P%P -u%u -g%g -- %E" (or dumping to /home/zzq/projects/jattack/core.432431)
#
# If you would like to submit a bug report, please visit:
#   https://github.com/AdoptOpenJDK/openjdk-support/issues
#
```

To run the demo, please run `./demo.sh`. Sample output:
```
Download JDK...
Build JAttack jar...
Install python packages...
1..3
[22:36:02E]su.__main__: bash: line 1: 1836850 Aborted                 (core dumped) /home/zzq/projects/jattack/.jattack/downloads/jdk-11.0.8+10/bin/java -cp /home/zzq/projects/jattack/tool/jattack-all.jar:/home/zzq/projects/jattack/.jattack/T/build -XX:TieredStopAtLevel=4 -XX:ErrorFile=/home/zzq/projects/jattack/.jattack/T/output/TGen1/he_err_pid%p.log -XX:ReplayDataFile=/home/zzq/projects/jattack/.jattack/T/output/TGen1/replay_pid%p.log TGen1 > /home/zzq/projects/jattack/.jattack/T/output/TGen1/java_env0.txt 2> /dev/null

not ok 1 - TGen1
  ---
  message: 'Found a potential crash bug'
  data: CrashBugData(type=<BugType.CRASH: 'crash'>, crashed_java_envs=[JavaEnv(java_home=PosixPath('/home/zzq/projects/jattack/.jattack/downloads/jdk-11.0.8+10'), java_opts=['-XX:TieredStopAtLevel=4'])])
  ...
ok 2 - TGen2
ok 3 - TGen3
```

## Requirements

- Linux with GNU Bash (tested on Ubuntu 20.04)
- JDK >=11
- Python 3.8

## Install

```bash
cd tool
./install.sh
```

The `install.sh` script builds JAttack jar, installs python packages
and creates an executable `jattack` in `tools`.

## Use

```bash
cd tool
./jattack --clz TEMPLATE_CLASS_NAME --n_gen NUM_OF_GENERATED_PROGRAMS \
    [--java_envs JAVA_ENVIRONMENTS_UNDER_TEST]
    [--src TEMPLATE_SOURCE_PATH]
    [--n_itrs NUM_OF_ITERATIONS_TO_TRIGGER_JIT]
    [--seed RANDOM_SEED]
```

### Examples of Run Commands:

- Provide only two required arguments `--clz` and `--n_gen`.

  ```bash
  ./tool/jattack --clz T --n_gen 3
  ```

  This command generates 3 programs from template `T.java` and uses
  the 3 generated programs to test default java environments found in
  `$JAVA_HOME` at level 4 and level 1, which are:
  - `$JAVA_HOME/bin/java -XX:TieredStopAtLevel=4`
  - `$JAVA_HOME/bin/java -XX:TieredStopAtLevel=1`

- Specify java environments and associated java options to be tested
  using `--java_envs`.

  ```bash
  ./tool/jattack --clz T --n_gen 3 \
      --java_envs "[\
          [/home/zzq/opt/jdk-11.0.15,[-Xbatch,-Xcomp,-XX:-TieredCompilation]],\
          [/home/zzq/opt/jdk-17.0.3,[-Xbatch,-Xcomp,-XX:TieredStopAtLevel=1]],\
          [/home/zzq/opt/jdk-17.0.3,[]]]"
  ```

  The `java_envs` argument is a list, which can be also appended using
  `--java_envs+=`, for example, the command above can be rewritten as:
  ```bash
  ./tool/jattack --clz T --n_gen 3 \
      --java_envs [[/home/zzq/opt/jdk-11.0.15,[-Xbatch,-Xcomp,-XX:-TieredCompilation]]] \
      --java_envs+=[[/home/zzq/opt/jdk-17.0.3,[-Xbatch,-Xcomp,-XX:TieredStopAtLevel=1]]] \
      --java_envs+=[[/home/zzq/opt/jdk-17.0.3,[]]]
  ```

  The `java_envs` argument can also be given using a config file, i.e.,
  ```bash
  ./tool/jattack --config config.yaml --clz T --n_gen 3
  ```
  where `config.yaml` is:
  ```yaml
  # config.yaml
  java_envs:
    -
      - /home/zzq/opt/jdk-11.0.15
      - - -Xbatch
        - -Xcomp
        - -XX:-TieredCompilation
    -
      - /home/zzq/opt/jdk-17.0.3
      - - -Xbatch
        - -Xcomp
        - -XX:TieredStopAtLevel=1
    -
      - /home/zzq/opt/jdk-17.0.3
      - []
  ```

  This command generates 3 programs from template `T.java` and uses
  the 3 generated programs to test given java environments with given
  options, which are
  - `/home/zzq/opt/jdk-11.0.15/bin/java -Xbatch -Xcomp -XX:-TieredCompilation`
  - `/home/zzq/opt/jdk-17.0.3/bin/java -Xbatch -Xcomp -XX:TieredStopAtLevel=1`
  - `/home/zzq/opt/jdk-17.0.3/bin/java`

### Full List of Arguments:
```
  -h, --help            Show this help message and exit.

  --config CONFIG       Path to a configuration file.

  --print_config[=flags]
                        Print the configuration after applying all other arguments and exit.
                        The optional flags are one or more keywords separated by comma which
                        modify the output. The supported flags are: comments, skip_default,
                        skip_null.

  --clz CLZ             the fully qualified class name of the template, separated with "."
                        (required, type: str)
  --n_gen N_GEN         the total number of generated programs (required, type: int)
  --src SRC             the path to the source file of the template, by default using
                        `./{clz}.java` (type: Union[str, null], default: null)
  --n_itrs N_ITRS       the number of iterations to trigger JIT (type: int, default: 100000)
  --seed SEED           the random seed used by JAttack during generation, fix this to
                        reproduce a previous generation. (type: Union[int, null], default:
                        null)
  --java_envs JAVA_ENVS, --java_envs+ JAVA_ENVS
                        the java environments to be differentially tested, which should be
                        provided as a list of a tuple of java home string and a list of java
                        option strings, e.g., `--java_envs=[/home/zzq/opt/jdk-11.0.15,[-XX:Tie
                        redStopAtLevel=4],/home/zzq/opt/jdk-17.0.3,[-XX:TieredStopAtLevel=1]]`
                        means we want to differentially test java 11 at level 4 and java 17 at
                        level 1. Note, the first java environment of the list will be used to
                        compile the template and generated programs, which means the version
                        of the first java environment has to be less than or equal to the
                        remaining ones. Also, the first java environment is used to run
                        JAttack itself, which means its version should be at least 11. By
                        default, $JAVA_HOME in the system with level 4 and level 1 will be
                        used, i.e., `[$JAVA_HOME,[-XX:TieredStopAtLevel=4],$JAVA_HOME,[-XX:Tie
                        redStopAtLevel=1]]` (type: Union[List[Tuple[str, List[str]]], null],
                        default: null)
```

### Output

JAttack's command-line output is in [TAP](https://testanything.org/)
format, so you can make it prettier using any TAP consumer, like
[tapview](https://gitlab.com/esr/tapview):
```
$ ./tool/jattack --clz T --n_gen 3 --seed 42 \
    --java_envs "[\
        [.jattack/downloads/jdk-11.0.8+10,[-XX:TieredStopAtLevel=4]],\
        [.jattack/downloads/jdk-11.0.8+10,[-XX:TieredStopAtLevel=1]]]" \
    | tapview
F..
not ok 1 - TGen1
  ---
  message: 'Found a potential crash bug'
  data: CrashBugData(type=<BugType.CRASH: 'crash'>, crashed_java_envs=[JavaEnv(java_home=PosixPath('/home/zzq/projects/jattack/.jattack/downloads/jdk-11.0.8+10'), java_opts=['-XX:TieredStopAtLevel=4'])])
  ...
3 tests, 1 failures.
```

After the run, a hidden directory `.jattack` is created under
current working directory as the following structure:
```
.jattack
    - logs # logs of runs
      - 1668918602126595408.log
    - T
      - build # Java class files
        - T.class
        - TGen1.class
        - TGen2.class
        - TGen3.class
      - gen # Generated programs from the template
        - TGen1.java
        - TGen2.java
        - TGen3.java
      - output # Outputs of generated programs executed on different java environments
        - TGen1
          - java_env0.txt # Output from execution on java_envs[0]
          - java_env1.txt # Output from execution on java_envs[1]
          - replay_pid2327972.log # replay data file of the crash
          - he_err_pid2327972.log # error data file of the crash
        - TGen2
          - java_env0.txt
          - java_env1.txt
        - TGen3
          - java_env0.txt
          - java_env1.txt
```

## Docs

The following steps build javadoc for JAttack jar. Please refer to
class `Boom` for how to use provided APIs to write your own template.

1. Build javadoc from source code.
   ```bash
   cd tool/api
   ./gradlew javadoc
   ```

2. Open `tool/api/build/docs/javadoc/index.html` in your favorite
   browser.

## Hall of Fame

Directory `bugs` contains all six JIT bugs we found using JAttack,
each of which contains a template program, a generated program and a
minimized program to expose the bug.

If you find JIT bugs using JAttack, we would be happy to add your
findings to this list. Please open a PR with a link to your bug.

- [JDK-8239244](https://bugs.openjdk.java.net/browse/JDK-8239244)
  (Login required): See
  [CVE-2020-14792](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-14792)
- [JDK-8258981](https://bugs.openjdk.java.net/browse/JDK-8258981): JVM
  crash with # Problematic frame: # V [libjvm.so+0xdc0df5]
  ok_to_convert(Node*, Node*)+0x15
- [JDK-8271130](https://bugs.openjdk.java.net/browse/JDK-8271130)
  (Login required): See
  [CVE-2022-21305](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2022-21305)
- [JDK-8271276](https://bugs.openjdk.java.net/browse/JDK-8271276): C2:
  Wrong JVM state used for receiver null check
- [JDK-8271459](https://bugs.openjdk.java.net/browse/JDK-8271459): C2:
  Missing NegativeArraySizeException when creating StringBuilder with
  negative capacity
- [JDK-8271926](https://bugs.openjdk.java.net/browse/JDK-8271926):
  Crash related to Arrays.copyOf with # Problematic frame: # V
  [libjvm.so+0xc1b83d] NodeHash::hash_delete(Node const*)+0xd

## Citation

If you use JAttack in your research, we request you to cite our
[ASE'22 paper](https://cptgit.github.io/dl/papers/zang22jattack.pdf)
(which won an ACM SIGSOFT Distinguished Paper Award). Thank you!

```bibtex
@inproceedings{zang22jattack,
  author = {Zang, Zhiqiang and Wiatrek, Nathaniel and Gligoric, Milos and Shi, August},
  title = {Compiler Testing using Template Java Programs},
  booktitle = {International Conference on Automated Software Engineering},
  pages = {To appear},
  year = {2022},
  doi = {10.1145/3551349.3556958},
}
```

## Contact

Let me ([Zhiqiang Zang](https://github.com/CptGit)) know if you have
any questions.
