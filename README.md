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

## Use

This demo shows how JAttack generates programs from a given template
`T.java` and then executes the generated programs for differential
testing of Java JIT compilers.

### Requirements

- Linux with GNU Bash (tested on Ubuntu 20.04)

Usage: `./demo.sh <class name> <number of generated programs>`

For example: `./demo.sh T 3`, which is also the default setting when
no argument is specified.

```bash
$ ./demo.sh
Download JDK...
Build JAttack...
Generate from T...
3 programs are generated in /home/zzq/jattack-master/.jattack/T/gen
Executing TGen1...
  At level4...
./demo.sh: line 115:  9670 Aborted                 (core dumped) java -cp "${CP}" ${EXTRA_JAVA_FLAGS} ${STOP_AT_LEVEL}${level} ${gen_clz} ${n_exec_itrs} > "${output_file}" 2>&1
ERROR: running TGen1 at level4. See /home/zzq/jattack-master/.jattack/T/output/TGen1/TGen1-level4.txt
  At level1...
TGen1: level4 differs with level1!
Executing TGen2...
  At level4...
  At level1...
Executing TGen3...
  At level4...
  At level1...
TGen1 failed! Potential JIT bugs!
```

## Docs

### Requirements

- JDK >=11

### Steps

1. Build javadoc from source code.
```bash
cd tool/api
./gradlew javadoc
```

2. Open `tool/api/build/docs/javadoc/index.html` in your favorite
   browser.

## Bugs

Directory `bugs` contains all six JIT bugs found by JAttack, each of
which contains a template program, a generated program and a minimized
program to expose the bug.

If you find bugs with JAttack, we would be happy to add them to this
list. Please open a PR with a link to your bug.

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
[ASE'22 paper](https://cptgit.github.io/dl/papers/zang22jattack.pdf).
Thank you!

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
