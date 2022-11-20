#!/usr/bin/env python

import logging
import os
import re
import seutil as su
import subprocess
import time
from jsonargparse import CLI
from natsort import natsorted
from pathlib import Path
from seutil.bash import BashError
from typing import List, NamedTuple, Tuple

# Constants.
_DIR = Path(os.path.dirname(os.path.realpath(__file__)))
CWD = Path.cwd()
DOT_DIR = CWD / ".jattack"
LOG_DIR = DOT_DIR / "logs"

JATTACK_JAR = _DIR / "jattack-all.jar"

# Set up logging
su.io.mkdir(LOG_DIR, parents=True)
log_file = Path(LOG_DIR / f"{time.time_ns()}.log")
print("See log file:", log_file)
su.log.setup(log_file=log_file, level_stderr=logging.WARNING, level_file=logging.DEBUG)
logger = su.log.get_logger(__name__, level=logging.DEBUG)

class BailOutError(RuntimeError):
    def __init__(self, msg: str):
        self.msg = msg
    #fed
#ssalc

class JavaEnv(NamedTuple):
    java_home: Path
    java_opts: List[str]
#ssalc

class Args:
    def __init__(
        self,
        clz: str,
        n_gen: int,
        src: Path,
        n_itrs: int,
        seed: int,
        java_envs: List[Tuple[str, List[str]]]
    ):
        self.clz = clz
        self.n_gen = n_gen
        self.src = src
        self.n_itrs = n_itrs
        self.seed = seed
        self.java_envs = [
            JavaEnv(
                Path(java_env[0]),
                java_env[1]
            ) for java_env in java_envs
        ]

        # Check all java environments are valid
        if len(self.java_envs) < 2:
            raise ValueError(
                "Expected at least 2 java environments for "
                "differential testing but found only "
                f"{len(self.java_envs)}")
        #fi
        for java_env in self.java_envs:
            if not (java_env.java_home / "bin" / "java").is_file():
                raise ValueError(
                    "java not found in given java home under test: "
                    f"{java_env.java_home / 'bin'}")
            #fi
            if not (java_env.java_home / "bin" / "javac").is_file():
                raise ValueError(
                    "javac not found in given java home under test: "
                    f"{java_env.java_home / 'bin'}")
            #fi
        #rof

        # Use the first java environment to compile and run JAttack
        # itself
        self.javac = self.java_envs[0].java_home / "bin" / "javac"
        self.java = self.java_envs[0].java_home / "bin" / "java"

        # Set up directories
        self.tmpl_dir = DOT_DIR / self.clz
        self.build_dir = self.tmpl_dir / "build"
        self.gen_dir = self.tmpl_dir / "gen"
        self.output_dir = self.tmpl_dir / "output"
    #fed
#ssalc

def exceute_and_test(
    tmpl_clz: str,
    gen_dir: Path,
    output_dir: Path,
    jattack_jar: Path,
    build_dir: Path,
    javac: Path,
    java_envs: List[JavaEnv]
) -> None:
    """
    Execute every generated program on different JIT compilers and       perform differential testing over results.

    :raises: BailOutError if something is wrong
    """
    # num_gen could be different with n_gen given by the user in the
    # case where a template defines a small search space while n_gen
    # is given a large number.
    all_gen_paths = [ Path(p) for p in natsorted(gen_dir.glob("*.java"))]
    num_gen = len(all_gen_paths)
    print_test_plan(num_gen)
    pkg = ".".join(tmpl_clz.split(".")[:-1])
    cp = str(jattack_jar) + os.pathsep + str(build_dir)

    # Execute every generated porgrams
    su.io.rm(output_dir)
    su.io.mkdir(output_dir, parents=True)
    for gen_i, gen_src in enumerate(all_gen_paths):
        test_number = gen_i + 1
        gen_clz = pkg + "." + gen_src.stem if pkg else gen_src.stem
        output_dir_per_gen = output_dir / gen_clz

        # Clean
        su.io.rm(output_dir_per_gen)
        su.io.mkdir(output_dir_per_gen, parents=True)

        # Compile
        try:
            bash_run(f"{javac} -cp {cp} {gen_src} -d {build_dir}")
        except BashError as e:
            logger.error(e)
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg="Compiling generated program failed")
            continue
        #yrt

        # Execute on all Javas.
        crashed_jes = []
        for je_i, je in enumerate(java_envs):
            java = je.java_home / "bin" / "java"
            opts= " ".join(je.java_opts)
            output_file = output_dir_per_gen / f"java_env{je_i}.txt"
            res = bash_run(
                f"{java} -cp {cp}"
                f" {opts}"
                f" -XX:ErrorFile={output_dir_per_gen}/he_err_pid%p.log"
                f" -XX:ReplayDataFile={output_dir_per_gen}/replay_pid%p.log"
                f" {gen_clz} >{output_file} 2>/dev/null"
            )
            if res.returncode != 0:
                logger.error(res.stderr)
                crashed_jes.append(je)
            #fi
        #rof
        if crashed_jes:
            # At least one java environment under test crashed
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg=f"crash at {crashed_jes}"
            )
            continue
        #fi

        # Compare results between every two java environments
        for i1 in range(0, len(java_envs)):
            for i2 in range(i1 + 1, len(java_envs)):
                diff_file = output_dir_per_gen / f"{i1}-{i2}-diff.txt"
                output_file1 = output_dir_per_gen / f"java_env{i1}.txt"
                output_file2 = output_dir_per_gen / f"java_env{i2}.txt"
                try:
                    bash_run(f"diff {output_file1} {output_file2} >{diff_file}")
                    # no diff, removing empty diff file
                    su.io.rm(diff_file)
                except BashError as e:
                    # Diff
                    print_not_ok(
                        test_number=test_number,
                        desc=gen_clz,
                        msg=f"diff between {java_envs[i1]} and {java_envs[i2]}"
                    )
                #yrt
            #rof
        #rof

        print_ok(test_number=test_number, desc=gen_clz)
    #rof
#fed

def generate(
    clz: str,
    n_gen: int,
    src: Path,
    n_itrs: int,
    seed: int,
    jattack_jar: Path,
    gen_dir: Path,
    tmpl_classpath: Path,
    java: Path
) -> None:
    """
    Generate programs from the given template using JAttack.

    :raises: BailOutError if JAttack throws any error or no reachable
                          hole in the template
    """
    # Clean
    su.io.rm(gen_dir)
    su.io.mkdir(gen_dir, parents=True)

    # Run JAttack
    try:
        bash_run(
            f"{java} -javaagent:{jattack_jar} -cp {tmpl_classpath}"
            f" jattack.driver.Driver"
            f" --clzName={clz}"
            f" --nOutputs={n_gen}"
            f" --srcPath={src}"
            f" --nInvocations={n_itrs}" +\
            (f" --seed={seed}" if seed else "") +\
            f" --outputDir={gen_dir}")
    except BashError as e:
        logger.error(e)
        raise BailOutError("Generating from template failed")
    #yrt

    # Literally no hole in the template or no hole reached in the
    # template during generation
    if list(gen_dir.glob("*0.java")):
        raise BailOutError("No reachable hole in the template!")
    #fi
#fed

def compile_template(src: Path, build_dir: Path, javac: Path) -> None:
    """
    Compile the given template.

    :raises: BailOutError if source file cannot be found or compiling
             fails
    """
    if not src.is_file():
        raise BailOutError(f"Template source file not found: {src}")
    #fi
    try:
        su.io.mkdir(build_dir, parents=True)
        bash_run(f"{javac} -cp {JATTACK_JAR} {src} -d {build_dir}")
    except BashError:
        logger.error(e)
        raise BailOutError("Compiling template failed")
    #yrt
#fed

def print_test_plan(num: int) -> None:
    """
    Print test plan as TAP format.
    """
    print(f"1..{num}")
#fed

def print_bail_out(msg: str) -> None:
    """
    Print \"bail out\" as TAP format.
    """
    print(f"Bail out! {msg}")

def print_not_ok(test_number: str, desc: str, msg: str) -> None:
    """
    Print \"not ok\": test point as TAP format.
    """
    print(f"not ok {test_number} - {desc}")
    print("  ---")
    print(f"  message: '{msg}'")
    print("  ...")

def print_ok(test_number: str, desc: str) -> None:
    """
    Print \"ok\" test point  as TAP format.
    """
    print(f"ok {test_number} - {desc}")

def bash_run(
    command: str,
    check_returncode: int = None,
    timeout: int = None
) -> subprocess.CompletedProcess:
    """
    Run a command in bash.
    """
    logger.info(f"Bash: {command}")
    res = su.bash.run(command, check_returncode=check_returncode,
    timeout=timeout)
    #logger.info(res.stdout)
    #logger.error(res.stderr)
    return res
#fed

def main(
    clz: str,
    n_gen: int,
    src: str = None,
    n_itrs: int = 100_000,
    seed: int = None,
    #  By default we use java in system path and compare level 4 and
    # level 1.
    java_envs: List[Tuple[str, List[str]]] = [
        (
            os.environ["JAVA_HOME"],
            ["-XX:TieredStopAtLevel=4"],
        ),
        (
            os.environ["JAVA_HOME"],
            ["-XX:TieredStopAtLevel=1"],
        ),
    ]
) -> None:
    """
    Main.

    :param clz: the fully qualified class name of the template,
        separated with \".\"
    :param n_gen: the total number of generated programs
    :param src: the path to the source file of the template, by default using `./{clz}.java`
    :param n_itrs: the number of iterations to trigeer JIT
    :param seed: the random seed used by JAttack during generation,
        fix this to reproduce a previous generation
    :param java_envs: the java environments to be differentially
        tested, which should be provided as a list of a tuple of java
        home string and a list of any java option strings, e.g.,
        `--java_envs=[/home/zzq/opt/jdk-11.0.15,[-XX:TieredStopAtLevel=4],/home/zzq/opt/jdk-17.0.3,[-XX:TieredStopAtLevel=1]]`
        means we want to differentially test java 11 at level 4 and
        java 17 at level 1.
        By default, $JAVA_HOME in the system environment with level 4
        and level 1 will be used.
        Note, the first java environment of the list will be used to
        compile the template and generated programs, which means the
        version of the first java environment has to be less than or
        equal to the remaining ones. Also, the first java environment
        is used to run JAttack itself, which means its version should
        be at least 11.
    """

    if src is None:
        src = CWD / f"{clz}.java"
    else:
        src = Path(src)
    #fi
    args = Args(
        clz=clz,
        n_gen=n_gen,
        src=src,
        n_itrs=n_itrs,
        seed=seed,
        java_envs=java_envs)

    try:
        compile_template(
            src=args.src,
            build_dir=args.build_dir,
            javac=args.javac)
        generate(
            clz=args.clz,
            n_gen=args.n_gen,
            src=args.src,
            n_itrs=args.n_itrs,
            seed=args.seed,
            jattack_jar=JATTACK_JAR,
            gen_dir=args.gen_dir,
            tmpl_classpath=args.build_dir,
            java=args.java)
        exceute_and_test(
            tmpl_clz=args.clz,
            gen_dir=args.gen_dir,
            output_dir=args.output_dir,
            jattack_jar=JATTACK_JAR,
            build_dir=args.build_dir,
            javac=args.javac,
            java_envs=args.java_envs)
    except BailOutError as e:
        print_bail_out(e.msg)
    #yrt
#fed

if __name__ == "__main__":
    CLI(main, as_positional=False)
#fi
