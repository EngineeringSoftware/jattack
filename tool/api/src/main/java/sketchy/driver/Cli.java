package sketchy.driver;

import sketchy.Config;
import sketchy.log.Log;
import sketchy.util.IOUtil;
import sketchy.util.Rand;

import java.util.LinkedList;

/**
 * Class to parse command-line arguments.
 */
public class Cli {

    private String key = "";
    private String value = "";

    public static void parseArgs(String[] args) {
        Cli cli = new Cli();
        for (String arg : args) {
            cli.readSingleArg(arg);
            cli.processSingleArg();
        }
        checkRequired();
        checkConsistent();
        initialize();
    }

    private static void initialize() {
        IOUtil.createDir(Config.outputDir);
    }

    private static void checkConsistent() {
        // TODO: automatically disable hotFilling and solverAid when
        //  detecting dynamicCollecting or saveHoleValues on and pop
        //  error if users explicitly enable either hotFilling or
        //  solverAid.
        if ((Config.dynamicCollecting || Config.saveHoleValues)
                && (Config.optHotFilling || Config.optSolverAid)) {
            throw new RuntimeException("optHotFilling and optSolverAid must be turned off when dynamicCollecting or saveHoleValues are turned on!");
        }
        if (Config.dynamicCollecting && !Config.isProfiling) {
            throw new RuntimeException("isProfiling must be turned on when dynamicCollecting is turned on!");
        }
    }

    private void readSingleArg(String arg) {
        if (!arg.startsWith("--")) {
            throw new RuntimeException("Argument does NOT start with --: " + arg + "!");
        }
        String opt = arg.substring(2); // remove "--"
        if (opt.isEmpty()) {
            throw new RuntimeException("Nothing following --: " + arg + "!");
        }
        int pos = opt.indexOf('=');
        if (pos == -1) {
            // no "="
            key = opt;
            value = "";
        } else {
            if (pos == opt.length() - 1) {
                throw new RuntimeException("Nothing following key: " + arg + "!");
            }
            key = opt.substring(0, pos);
            value = opt.substring(pos + 1);
        }
    }

    private void processSingleArg() {
        // TODO: check value format and range
        switch (key) {
        case "ss": {
            switch (value) {
            case "systematic": {
                Config.ss = SearchStrategy.SYSTEMATIC;
                break;
            }
            case "random": {
                Config.ss = SearchStrategy.RANDOM;
                break;
            }
            case "smart": {
                Config.ss = SearchStrategy.SMART;
                break;
            }
            default:
                throw new RuntimeException("Unrecognized search strategy: "
                        + value + "!");
            }
            break;
        }
        case "seed": {
            long seed = Long.parseLong(value);
            Config.seed = seed;
            Driver.rand = new Rand(seed);
            break;
        }
        case "isExhaustive": {
            Config.isExhaustive = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "nOutputs": {
            Config.nOutputs = Integer.parseInt(value);
            break;
        }
        case "nInvocations": {
            Config.nInvocations = Integer.parseInt(value);
            break;
        }
        case "ints": {
            Config.ints = new LinkedList<>();
            for (String s : readArgAsArray(value, "+")) {
                Config.ints.add(Integer.parseInt(s));
            }
            break;
        }
        case "longs": {
            Config.longs = new LinkedList<>();
            for (String s : readArgAsArray(value, "+")) {
                Config.longs.add(Long.parseLong(s));
            }
            break;
        }
        case "doubles": {
            Config.doubles = new LinkedList<>();
            for (String s : readArgAsArray(value, "+")) {
                Config.doubles.add(Double.parseDouble(s));
            }
            break;
        }
        case "clzName": {
            Config.sketchClzFullName = value;
            break;
        }
        case "srcPath": {
            Config.sketchSrcPath = value;
            break;
        }
        case "transformAPI": {
            Config.transformApi = Boolean.parseBoolean(value);
            break;
        }
        case "outputDir": {
            Config.outputDir = value;
            break;
        }
        case "outputClzName": {
            Config.outputWOTransformedClzName = value;
            break;
        }
        case "outputPostfix": {
            Config.outputClzNamePostfix = value;
            break;
        }
        case "profiling": {
            Config.isProfiling = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "profilingFile": {
            Config.profilingFile = value;
            break;
        }
        case "optHotFilling": {
            Config.optHotFilling = Boolean.parseBoolean(value);
            break;
        }
        case "optStopEarly": {
            Config.optStopEarly = Boolean.parseBoolean(value);
            break;
        }
        case "optSolverAid": {
            Config.optSolverAid = Boolean.parseBoolean(value);
            break;
        }
        case "debug": {
            if (value.isEmpty() || Boolean.parseBoolean(value)) {
                Log.setLevel("debug");
            }
            break;
        }
        case "logging": {
            Log.setLevel(value);
            break;
        }
        case "keepPkg": {
            Config.keepPkg = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "discardPkg": {
            Config.keepPkg = !value.isEmpty() && !Boolean.parseBoolean(value);
            break;
        }
        case "nRepeatedTrials": {
            Config.maxRepeatedTrialsAllowed = Integer.parseInt(value);
            break;
        }
        case "allowNonCompilableOutput": {
            Config.allowNonCompilableOutput = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "dynamicCollecting": {
            Config.dynamicCollecting = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "disableOutput": {
            Config.disableOutput = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "saveHoleValues": {
            Config.saveHoleValues = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "holeValuesFile": {
            Config.holeValuesFile = value;
            break;
        }
        case "countInvalidArrIdxException": {
            Config.countInvalidArrIdxException = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "invalidArrIdxExceptionFile": {
            Config.invalidArrIdxExceptionFile = value;
            break;
        }
        case "staticGen": {
            Config.staticGen = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "trackHoles": {
            Config.trackHoles = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        }
        case "trackHolesFile": {
            Config.trackHolesFile = value;
            break;
        }
        case "mimicExecution":
            Config.mimicExecution = value.isEmpty() || Boolean.parseBoolean(value);
            break;
        default:
            throw new RuntimeException("Unrecognized option: " + key);
        }
    }

    private static void checkRequired() {
        if (Config.sketchClzFullName == null) {
            throw new RuntimeException("--clzName is required!");
        }
        if (Config.sketchSrcPath == null) {
            throw new RuntimeException("--srcPath is required!");
        }
        if (!Config.transformApi && Config.outputWOTransformedClzName == null) {
            throw new RuntimeException("--outputClzName is required!");
        }
    }

    private static String[] readArgAsArray(String arg, String del) {
        return arg.isEmpty() ?
                new String[0] :
                arg.split("[" + del + "\\s+]");
    }
}
