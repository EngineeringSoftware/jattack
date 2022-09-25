package sketchy.compiler;

import sketchy.Config;
import sketchy.data.Data;
import sketchy.bytecode.VariableAnalyzer;
import sketchy.driver.Driver;
import sketchy.log.Log;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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

    public ClassLoader compileAndGetLoader(String className, String code)
            throws CompilationException {
        return compileAndGetLoader(className, code, true);
    }

    public ClassLoader compileAndGetLoader(String className,
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
        return newClassLoader();
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

    private ClassLoader newClassLoader() {
        return new InMemoryClassLoader(classBytes, parentCl);
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
     * <p>
     * https://stackoverflow.com/questions/30412604/get-real-classpath-when-running-java-application-with-mvn-execjava
     */
    private static String getClassPath(ClassLoader cl) {
        StringBuilder cp = new StringBuilder();
        ClassLoader sys = ClassLoader.getSystemClassLoader();
        for (; cl != null & cl != sys; cl = cl.getParent())
            if (cl instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                for (URL url : ucl.getURLs()) {
                    Log.debug(url);
                    cp.append(File.pathSeparator).append(url.getPath());
                }
            }
        return cp.length() == 0 ? null : cp.substring(1);
    }
}
