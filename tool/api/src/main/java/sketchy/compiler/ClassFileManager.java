package sketchy.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.util.Map;

/**
 * File Manager for our in-memory compiler.
 * <p>
 * https://stackoverflow.com/questions/12173294/compile-code-fully-in-memory-with-javax-tools-javacompiler
 * https://javapracs.blogspot.com/2011/06/dynamic-in-memory-compilation-using.html
 * <p>
 * We stopped using the approach in the link
 * https://atamur.blogspot.com/2009/10/using-built-in-javacompiler-with-custom.html
 * because it breaks the normal class loading so the class loader
 * cannot find sketchy.data.Data class.
 */
public class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private ClassBytes classBytes;

    /**
     * Will initialize the manager with the specified
     * standard java file manager
     */
    public ClassFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
        classBytes = new ClassBytes();
    }

    @Override
    public void close() throws IOException {
        super.close();
        classBytes = null;
    }

    public Map<String, byte[]> getBytes() {
        return classBytes.getBytesByClass();
    }

    public ClassBytes getClassBytes() {
        return classBytes;
    }

    /**
     * Gives the compiler an instance of the JavaClassObject
     * so that the compiler can write the byte code into it.
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            return new JavaClassObject(className, classBytes);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }
}
