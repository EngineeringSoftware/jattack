#!/usr/bin/env python

import logging
import os
import re
import seutil as su
import subprocess
import time
from jsonargparse import CLI
from pathlib import Path

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
        su.io.mkdir(self.build_dir, parents=True)
        su.io.mkdir(self.gen_dir, parents=True)
        su.io.mkdir(self.output_dir, parents=True)
#ssalc

def compile_template(src: Path, build_dir: Path) -> None:
    """Compile the given template.

    :raises: su.bash.BashError if source file cannot be found or compiling fails.
    """
    if not src.is_file():
        raise BailOutError(f"File not found: {src}")
    #fi
    try:
        bash_run(f"javac -cp {JATTACK_JAR} {src} -d {build_dir}")
    except su.bash.BashError:
        raise BailOutError("Compiling template failed")
    #yrt

def require_jattack_jar() -> None:
    """Require JAttack jar.
    Will build the jar if it does not exist.
    """
    if not JATTACK_JAR.is_file():
        build_jattack_jar()
    #fi
#fed

def build_jattack_jar() -> None:
    """Build JAttack jar.
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
    bash_run("./gradlew -q clean shadowJar")
    jar = src_dir / "build" / "libs" / f"jattack-{version}-all.jar"
    bash_run(f"cp {jar} {JATTACK_JAR}")
    os.chdir(CWD)
#fed

def print_bail_out(msg: str) -> None:
    """Print \"bail out\" as TAP format.
    """
    print(f"Bail out! {msg}")

def print_not_ok(test_number: str, msg: str, desc: str) -> None:
    """Print \"not ok\": test point as TAP format.
    """
    print(f"not ok {test_number} - {desc}")
    print("  ---")
    print("  message: '$msg'")
    print("  ...")

def print_ok(tets_number: str, desc: str) -> None:
    """Print \"ok\" tets point  as TAP format.
    """
    print(f"ok {test_number} - {desc}")

def bash_run(
    command: str,
    check_returncode: int = 0,
    timeout: int = None) -> subprocess.CompletedProcess:
    """Run a command in bash.
    """
    logger.info(f"Bash: {command}")
    return su.bash.run(command, check_returncode=check_returncode, timeout=timeout)
#fed

def main(
    clz: str,
    n_gen: int,
    src: str = "{clz}.java",
    n_itrs: int = 100_000,
    seed: int = None) -> None:
    """Main.

    :param clz: the fully qualified class name of the template, separated with \".\"
    :param n_gen: the total number of generated programs
    :param src: the path to the source file of the template
    :param n_itrs: the number of iterations to trigeer JIT
    :param seed: random seed used during generation
    """
    args = Args(clz, n_gen, f"{clz}.java", n_itrs, seed)
    require_jattack_jar()

    try:
        compile_template(args.src, args.build_dir)
    except BailOutError as e:
        print_bail_out(e.msg)
    #yrt
#fed

if __name__ == "__main__":
    CLI(main, as_positional=False)
#fi
