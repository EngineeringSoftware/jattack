package sketchy.compiler;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Class to represent a compiled .class file in memory.
 * <p>
 * https://javapracs.blogspot.com/2011/06/dynamic-in-memory-compilation-using.html
 */
public class JavaClassObject extends SimpleJavaFileObject {

    private final ClassBytes classBytes;
    private final String className;

    /**
     * Registers the compiled class object under URI
     * containing the class full name.
     */
    public JavaClassObject(String name, ClassBytes classBytes) {
        super(URI.create("string:///" + name.replace('.', '/')
                + Kind.CLASS.extension), Kind.CLASS);
        this.className = name;
        this.classBytes = classBytes;
    }

    /**
     * Will provide the compiler with an output stream that leads
     * to our byte array. This way the compiler will write everything
     * into the byte array that we will instantiate later
     */
    @Override
    public OutputStream openOutputStream() {
        return new FilterOutputStream(new ByteArrayOutputStream()) {
            public void close() throws IOException {
                out.close();
                classBytes.put(className, ((ByteArrayOutputStream) out).toByteArray());
            }
        };
    }
}
