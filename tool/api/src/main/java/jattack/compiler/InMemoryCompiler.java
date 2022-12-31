package jattack.compiler;

import jattack.Config;
import jattack.data.Data;
import jattack.bytecode.VariableAnalyzer;
import jattack.driver.Driver;
import jattack.log.Log;
import jattack.util.TypeUtil;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to compile and load a given class in memory.
 * <p>
 * https://stackoverflow.com/questions/12173294/compile-code-fully-in-memory-with-javax-tools-javacompiler
 */
public class InMemoryCompiler {

    private final JavaCompiler compiler;
    private final ClassLoader parentCl;
    private final List<String> options;

    private ClassBytes classBytes;

    public InMemoryCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
        parentCl = this.getClass().getClassLoader();
        String classPath = getClassPath(parentCl);
        options = new LinkedList<>(Arrays.asList("-g:vars")); // debugging information with local variables
        if (classPath != null) {
            options.add("-cp");
            options.add(classPath);
        }
    }

    /**
     * Compiles and then returns if compilation succeeds.
     */
    public ClassBytes compile(String className, String code)
            throws CompilationException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaSourceFromString file = new JavaSourceFromString(className, code);
        ClassFileManager fileManager = new ClassFileManager(stdFileManager);

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, options, null, compilationUnits);
        boolean success = task.call();

        // Compiling failed
        if (!success) {
            printDiagnosticInfo(diagnostics);
            throw new CompilationException("Compilation failed: " + className);
        }
        classBytes = fileManager.getClassBytes();
        try {
            fileManager.close();
        } catch (IOException e) {
            throw new RuntimeException("IO error when closing fileManager: " + e);
        }
        return classBytes;
    }

    public ClassBytes getClassBytes() {
        return classBytes;
    }

    /**
     * Compile the given source code and redefine the given class
     * using the obtained bytecode from compilation.
     * <p>
     * If the class is not in the classpath, for example, it is
     * created on the fly, then we just define it using a new
     * classloader and return the new classloader.
     * @param className the given class to redefine
     * @param code the source code to compile
     * @return the classloader associated with the given class
     * @throws CompilationException when the given source code could
     * not compile
     */
    public ClassLoader compileAndRedefine(String className, String code)
            throws CompilationException {
        return compileAndRedefine(className, code, true);
    }

    public ClassLoader compileAndRedefine(String className,
            String code, boolean transformBytecode)
            throws CompilationException {
        if (Config.isProfiling) {
            long t0 = System.currentTimeMillis();
            compile(className, code);
            Driver.totalCompileTime += System.currentTimeMillis() - t0;
        } else {
            compile(className, code);
        }
        if (transformBytecode) {
            transformBytecode();
        }
        return redefine(classBytes);
    }

    private void transformBytecode() {
        Data.resetOffsetsOfEvals();
        Data.resetLocalVars();
        for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
            String className = entry.getKey();
            byte[] bytes = entry.getValue();
            classBytes.put(className, VariableAnalyzer.transformBytecode(className, bytes));
        }
    }

    public ClassLoader redefine(ClassBytes classBytes) {
        List<ClassDefinition> classDefinitions = new LinkedList<>();
        for (Map.Entry<String, byte[]> entry: classBytes.entrySet()) {
            String name = entry.getKey();
            byte[] bytes = entry.getValue();
            try {
                Class<?> clz = TypeUtil.loadClz(name, false, parentCl);
                classDefinitions.add(new ClassDefinition(clz, bytes));
            } catch (ClassNotFoundException e) {
                // The class is not at classpath, produced in
                // memory. We will load it using a
                // new classloader.
                // This should only happen while Config.staticGen is
                // true, and we want to compile the class
                // HoleExtractor created at runtime.
                Log.debug("Defining class " + name + " from memory...");
                return new InMemoryClassLoader(classBytes, parentCl);
            }
        }

        try {
            Log.debug("Redefining classes " + classBytes.getClassNames());
            Agent.getInst().redefineClasses(classDefinitions.toArray(ClassDefinition[]::new));
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
        return parentCl;
    }

    public Set<String> getCompiledClassNames() {
        return classBytes.getClassNames();
    }

    private void printDiagnosticInfo(DiagnosticCollector<JavaFileObject> diagnostics) {
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.err.format("%s:%d: %s: %s%n",
                    diagnostic.getSource().toUri(),
                    diagnostic.getLineNumber(),
                    diagnostic.getCode(),
                    diagnostic.getMessage(null));
        }
    }

    /**
     * Get the class paths of a given class loader.
     */
    private static String getClassPath(ClassLoader cl) {
        return getAgentJarFilePath() +
               File.pathSeparator +
               // TODO: could not get classpath at all cases but it
               // should be fine for now.
               System.getProperty("java.class.path");
    }

    /**
     * Get the path as String of javaagent jar.
     */
    private static String getAgentJarFilePath() {
        try {
            return Paths.get(Agent.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
