#!/usr/bin/env python

import logging
import os
import seutil as su
import subprocess
import sys
import time
from collections import defaultdict
from dataclasses import dataclass
from enum import Enum
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

JAVA_OPTS = [
    "--add-opens java.base/java.lang=ALL-UNNAMED",
    "--add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED",
]

# Set up logging
su.io.mkdir(LOG_DIR, parents=True)
log_file = Path(LOG_DIR / f"{time.time_ns()}.log")
su.log.setup(
    log_file=log_file,
    level_stderr=logging.WARNING,
    level_file=logging.DEBUG)
logger = su.log.get_logger(__name__)
logger.info(f"See log file: {log_file}")

class BailOutError(RuntimeError):
    def __init__(self, msg: str):
        self.msg = msg
    #fed
#ssalc

class JavaEnv(NamedTuple):
    java_home: Path
    java_opts: List[str]
#ssalc

class BugType(Enum):
    """
    Bug Type.
    """
    CRASH = "crash"
    DIFF = "diff"
#ssalc

@dataclass
class BugData():
    """
    Data of a bug.
    """
    type: BugType
#ssalc

@dataclass
class CrashBugData(BugData):
    """
    Data of a crash bug.
    """
    crashed_java_envs: List[JavaEnv]

    def __init__(self, crashed_java_envs: List[JavaEnv]):
        self.crashed_java_envs = crashed_java_envs
        self.type = BugType.CRASH
    #fed
#ssalc

@dataclass
class DiffBugData(BugData):
    """
    Data of a diff bug.
    """
    diff_groups: List[List[JavaEnv]]

    def __init__(self, diff_groups: List[List[JavaEnv]]):
        self.diff_groups = diff_groups
        self.type = BugType.DIFF
    #fed
#ssalc

class Args:
    def __init__(
        self,
        clz: str,
        n_gen: int,
        src: str,
        n_itrs: int,
        seed: int,
        java_envs: List[Tuple[str, List[str]]]
    ):
        self.clz = clz
        self.n_gen = n_gen
        self.n_itrs = n_itrs
        self.seed = seed

        if src is None:
            self.src = CWD / f"{clz.split('.')[-1]}.java"
        else:
            self.src = Path(src)
        #fi

        if java_envs is None:
            java_home = os.environ.get("JAVA_HOME")
            if java_home is None:
                raise ValueError(
                    "JAVA_HOME environment variable is not set. Please "
                    "either set it or explicitly pass `--java_envs` "
                    "argument.")
            #fi
            # By default we use java in $JAVA_HOME and compare level 4 and
            # level 1.
            java_envs = [
                (
                    os.environ["JAVA_HOME"],
                    ["-XX:TieredStopAtLevel=4"],
                ),
                (
                    os.environ["JAVA_HOME"],
                    ["-XX:TieredStopAtLevel=1"],
                ),
            ]
        #fi
        self.java_envs = [
            JavaEnv(
                Path(java_env[0]),
                java_env[1]
            ) for java_env in java_envs
        ]

        # Check all java environments are valid
        if len(self.java_envs) == 0:
            raise ValueError(
                "No java environments are provided for testing.")
        #fi
        if len(self.java_envs) == 1:
            logger.info(
                f"Only one java environment is provided: {self.java_envs[0]}."
                " Differential testing will not work. Only crash will be reported.")
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
) -> bool:
    """
    Execute every generated program on different JIT compilers and
    perform differential testing over results.
    Return true if all tests pass otherwise return false.

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
    all_tests_pass = True
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
            bash_run(
                f"{javac} -cp {cp} {gen_src} -d {build_dir}",
                check_returncode=0)
        except BashError as e:
            all_tests_pass = False
            logger.error(e)
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg="Compiling generated program failed")
            continue
        #yrt

        # Execute on all java environments.
        crashed_jes = []
        for je_i, je in enumerate(java_envs):
            java = je.java_home / "bin" / "java"
            opts= " ".join(je.java_opts)
            output_file = output_dir_per_gen / f"java_env{je_i}.txt"
            res = bash_run(
                f"{java} -cp {cp}" +\
                " " + " ".join(JAVA_OPTS) +\
                f" {opts}"
                f" -XX:ErrorFile={output_dir_per_gen}/he_err_pid%p.log"
                f" -XX:ReplayDataFile={output_dir_per_gen}/replay_pid%p.log"
                f" {gen_clz} >{output_file}"
            )
            if res.returncode != 0:
                logger.error(res.stderr)
                crashed_jes.append(je)
            #fi
        #rof
        if crashed_jes:
            # At least one java environment under test crashed
            all_tests_pass = False
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg=f"Found a potential crash bug",
                data=CrashBugData(crashed_java_envs=crashed_jes)
            )
            continue
        #fi

        # Compare outputs from all java environments
        jes_by_output = defaultdict(list)
        for je_i, je in enumerate(java_envs):
            with open(output_dir_per_gen / f"java_env{je_i}.txt") as output_file:
                output = output_file.read()
                jes_by_output[output].append(je)
            #htiw
        #rof
        if len(jes_by_output) > 1:
            # Diff
            all_tests_pass = False
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg=f"Found a potential diff bug",
                data=DiffBugData(diff_groups=list(jes_by_output.values()))
            )
            continue
        #fi

        print_ok(test_number=test_number, desc=gen_clz)
    #rof
    return all_tests_pass
#fed

def generate(
    *args, # extra options for jattack jar
    clz: str,
    n_gen: int,
    src: Path,
    n_itrs: int,
    seed: int,
    jattack_jar: Path,
    gen_dir: Path,
    tmpl_classpath: Path,
    java: Path,
    gen_suffix: str = "Gen",
    extra_java_opts: List[str] = [],
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
        res = bash_run(
            f"{java} -javaagent:{jattack_jar} -cp {tmpl_classpath}" +\
            " " + " ".join(JAVA_OPTS) +\
            " " + " ".join(extra_java_opts) +\
            f" jattack.driver.Driver"
            f" --clzName={clz}"
            f" --nOutputs={n_gen}"
            f" --srcPath={src}"
            f" --nInvocations={n_itrs}" +\
            (f" --seed={seed}" if seed else "") +\
            f" --outputDir={gen_dir}" +\
            f" --outputPostfix={gen_suffix}"
            " " + " ".join(args) +\
            " 2>&1",
            check_returncode=0)
        print(res.stdout, end="")
    except BashError as e:
        logger.error(e)
        raise BailOutError("Generating from template failed")
    #yrt

    # Literally no hole in the template or no hole reached in the
    # template during generation
    if list(gen_dir.glob(f"*{gen_suffix}0.java")):
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
        bash_run(
            f"{javac} -cp {JATTACK_JAR} {src} -d {build_dir}",
            check_returncode=0)
    except BashError as e:
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
#fed

def print_not_ok(
    test_number: int,
    desc: str,
    msg: str,
    data: BugData = None) -> None:
    """
    Print \"not ok\": test point as TAP format.
    """
    print(f"not ok {test_number} - {desc}")
    print("  ---")
    print(f"  message: '{msg}'")
    print(f"  data: {data}")
    print("  ...")
#fed

def print_ok(test_number: int, desc: str) -> None:
    """
    Print \"ok\" test point  as TAP format.
    """
    print(f"ok {test_number} - {desc}")
#fed

def bash_run(
    command: str,
    check_returncode: int = None,
    timeout: int = None
) -> subprocess.CompletedProcess:
    """
    Run a command in bash.
    """
    logger.debug(f"Bash: {command}")
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
    java_envs: List[Tuple[str, List[str]]] = None
) -> None:
    """
    Main.

    :param clz: the fully qualified class name of the template,
        separated with \".\"
    :param n_gen: the total number of generated programs
    :param src: the path to the source file of the template.
        By default, `./{clz}.java` is used.
    :param n_itrs: the number of iterations to trigger JIT
    :param seed: the random seed used by JAttack during generation,
        fix this to reproduce a previous generation.
    :param java_envs: the java environments to be differentially
        tested, which should be provided as a list of a tuple of java
        home string and a list of java option strings, e.g.,
        `--java_envs=[[/home/zzq/opt/jdk-11.0.15,[-XX:TieredStopAtLevel=4]],[/home/zzq/opt/jdk-17.0.3,[-XX:TieredStopAtLevel=1]]]`
        means we want to differentially test java 11 at level 4 and
        java 17 at level 1.
        Note, the first java environment of the list will be used to
        compile the template and generated programs, which means the
        version of the first java environment has to be less than or
        equal to the remaining ones. Also, the first java environment
        is used to run JAttack itself, which means its version should
        be at least 11.
        By default, $JAVA_HOME in the system with level 4 and level 1
        are used, i.e.,
        `--java_envs=[[$JAVA_HOME,[-XX:TieredStopAtLevel=4]],[$JAVA_HOME,[-XX:TieredStopAtLevel=1]]]`
    """
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
        res = exceute_and_test(
            tmpl_clz=args.clz,
            gen_dir=args.gen_dir,
            output_dir=args.output_dir,
            jattack_jar=JATTACK_JAR,
            build_dir=args.build_dir,
            javac=args.javac,
            java_envs=args.java_envs)
        if not res:
            sys.exit(1)
        #fi
    except BailOutError as e:
        print_bail_out(e.msg)
        sys.exit(1)
    #yrt
#fed

if __name__ == "__main__":
    CLI(main, as_positional=False)
#fi
