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

class Args:
    def __init__(
        self,
        clz: str,
        n_gen: int,
        src: str,
        n_itrs: int,
        seed: int
    ):
        self.clz = clz
        self.n_gen = n_gen
        self.src = Path(src)
        self.n_itrs = n_itrs
        self.seed = seed

        self.tmpl_dir = DOT_DIR / self.clz
        self.build_dir = self.tmpl_dir / "build"
        self.gen_dir = self.tmpl_dir / "gen"
        self.output_dir = self.tmpl_dir / "output"

        # Make directories
        su.io.mkdir(self.output_dir, parents=True)
#ssalc

def exceute_and_test(
    tmpl_clz: str,
    gen_dir: Path,
    output_dir: Path,
    jattack_jar: Path,
    build_dir: Path
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
    for i, gen_src in enumerate(all_gen_paths):
        test_number = i + 1
        gen_clz = pkg + "." + gen_src.stem if pkg else gen_src.stem
        output_dir_per_gen = output_dir / gen_clz

        # Clean
        su.io.rm(output_dir_per_gen)
        su.io.mkdir(output_dir_per_gen, parents=True)

        # Compile
        try:
            bash_run(f"javac -cp {cp} {gen_src} -d {build_dir}")
        except BashError as e:
            logger.warning(e)
            print_not_ok(
                test_number=test_number,
                desc=gen_clz,
                msg="Compiling generated program failed")
            continue
        #yrt

        # Execute on all JIT compilers.
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
    tmpl_classpath: Path
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
            f"java -javaagent:{jattack_jar} -cp {tmpl_classpath}"
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

def compile_template(src: Path, build_dir: Path) -> None:
    """
    Compile the given template.

    :raises: BailOutError if source file cannot be found or compiling
             fails
    """
    if not src.is_file():
        raise BailOutError(f"File not found: {src}")
    #fi
    try:
        su.io.mkdir(build_dir, parents=True)
        bash_run(f"javac -cp {JATTACK_JAR} {src} -d {build_dir}")
    except BashError:
        logger.error(e)
        raise BailOutError("Compiling template failed")
    #yrt
#fed

def require_jattack_jar() -> None:
    """
    Require JAttack jar.
    Will build the jar only if it does not exist.
    """
    if not JATTACK_JAR.is_file():
        build_jattack_jar()
    #fi
#fed

def build_jattack_jar() -> None:
    """
    Build JAttack jar.

    :raises BailOutError if buildinfg JAttack jar fails
    """
    logger.info(f"Build JAttack jar.")
    src_dir = _DIR / "api"
    with open(src_dir / "build.gradle") as f:
        for line in f:
            res = re.search('^version (.*)$', line)
            if res:
                version = res.group(1)
            #fi
        #rof
    #htiw
    re.compile = src_dir / "build.gradle"
    os.chdir(src_dir)
    try:
        bash_run("./gradlew -q clean shadowJar")
    except BashError as e:
        logger.error(e)
        raise BailOutError
    #yrt
    jar = src_dir / "build" / "libs" / f"jattack-{version}-all.jar"
    bash_run(f"cp {jar} {JATTACK_JAR}")
    os.chdir(CWD)
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

def print_ok(tets_number: str, desc: str) -> None:
    """
    Print \"ok\" tets point  as TAP format.
    """
    print(f"ok {test_number} - {desc}")

def bash_run(
    command: str,
    check_returncode: int = 0,
    timeout: int = None
) -> subprocess.CompletedProcess:
    """
    Run a command in bash.
    """
    logger.info(f"Bash: {command}")
    return su.bash.run(command, check_returncode=check_returncode,
    timeout=timeout)
#fed

def main(
    clz: str,
    n_gen: int,
    src: str = "{clz}.java",
    n_itrs: int = 100_000,
    seed: int = None
) -> None:
    """
    Main.

    :param clz: the fully qualified class name of the template,
                separated with \".\"
    :param n_gen: the total number of generated programs
    :param src: the path to the source file of the template
    :param n_itrs: the number of iterations to trigeer JIT
    :param seed: random seed used during generation
    """
    args = Args(clz, n_gen, f"{clz}.java", n_itrs, seed)
    require_jattack_jar()

    try:
        compile_template(src=args.src, build_dir=args.build_dir)
        """ generate(
            clz=args.clz,
            n_gen=args.n_gen,
            src=args.src,
            n_itrs=args.n_itrs,
            seed=args.seed,
            jattack_jar=JATTACK_JAR,
            gen_dir=args.gen_dir,
            tmpl_classpath=args.build_dir) """
        exceute_and_test(
            tmpl_clz=args.clz,
            gen_dir=args.gen_dir,
            output_dir=args.output_dir,
            jattack_jar=JATTACK_JAR,
            build_dir=args.build_dir)
    except BailOutError as e:
        print_bail_out(e.msg)
    #yrt
#fed

if __name__ == "__main__":
    CLI(main, as_positional=False)
#fi
